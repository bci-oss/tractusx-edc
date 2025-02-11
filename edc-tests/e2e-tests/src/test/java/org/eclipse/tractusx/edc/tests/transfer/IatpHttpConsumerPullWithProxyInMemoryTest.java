/*
 *  Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - initial API and implementation
 *
 */

package org.eclipse.tractusx.edc.tests.transfer;

import jakarta.json.JsonObject;
import org.eclipse.edc.iam.did.spi.document.DidDocument;
import org.eclipse.edc.iam.did.spi.resolution.DidResolverRegistry;
import org.eclipse.edc.identityhub.spi.generator.PresentationCreatorRegistry;
import org.eclipse.edc.identityhub.spi.model.VerifiableCredentialResource;
import org.eclipse.edc.identityhub.spi.store.CredentialStore;
import org.eclipse.edc.identitytrust.model.CredentialFormat;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.tractusx.edc.did.DidExampleResolver;
import org.eclipse.tractusx.edc.lifecycle.ParticipantRuntime;
import org.eclipse.tractusx.edc.lifecycle.tx.iatp.DataspaceIssuer;
import org.eclipse.tractusx.edc.lifecycle.tx.iatp.IatpParticipant;
import org.eclipse.tractusx.edc.lifecycle.tx.iatp.SecureTokenService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.tractusx.edc.helpers.PolicyHelperFunctions.TX_CREDENTIAL_NAMESPACE;
import static org.eclipse.tractusx.edc.helpers.PolicyHelperFunctions.frameworkPolicy;

@EndToEndTest
public class IatpHttpConsumerPullWithProxyInMemoryTest extends AbstractHttpConsumerPullWithProxyTest {

    protected static final DataspaceIssuer DATASPACE_ISSUER_PARTICIPANT = new DataspaceIssuer();
    protected static final SecureTokenService STS_PARTICIPANT = new SecureTokenService();

    protected static final IatpParticipant PLATO_IATP = new IatpParticipant(PLATO, STS_PARTICIPANT.stsUri());

    @RegisterExtension
    protected static final ParticipantRuntime PLATO_RUNTIME = new ParticipantRuntime(
            ":edc-tests:runtime:iatp:runtime-memory-iatp-ih",
            PLATO.getName(),
            PLATO.getBpn(),
            PLATO_IATP.iatpConfiguration(SOKRATES)
    );

    protected static final IatpParticipant SOKRATES_IATP = new IatpParticipant(SOKRATES, STS_PARTICIPANT.stsUri());

    @RegisterExtension
    protected static final ParticipantRuntime SOKRATES_RUNTIME = new ParticipantRuntime(
            ":edc-tests:runtime:iatp:runtime-memory-iatp-ih",
            SOKRATES.getName(),
            SOKRATES.getBpn(),
            SOKRATES_IATP.iatpConfiguration(PLATO)
    );

    @RegisterExtension
    protected static final ParticipantRuntime STS_RUNTIME = new ParticipantRuntime(
            ":edc-tests:runtime:iatp:runtime-memory-sts",
            STS_PARTICIPANT.getName(),
            STS_PARTICIPANT.getBpn(),
            STS_PARTICIPANT.stsConfiguration(SOKRATES_IATP, PLATO_IATP)
    );

    @BeforeAll
    static void prepare() {

        // create the DIDs cache
        var dids = new HashMap<String, DidDocument>();
        dids.put(DATASPACE_ISSUER_PARTICIPANT.didUrl(), DATASPACE_ISSUER_PARTICIPANT.didDocument());
        dids.put(SOKRATES_IATP.didUrl(), SOKRATES_IATP.didDocument());
        dids.put(PLATO_IATP.didUrl(), PLATO_IATP.didDocument());

        configureParticipant(SOKRATES_IATP, SOKRATES_RUNTIME, dids);
        configureParticipant(PLATO_IATP, PLATO_RUNTIME, dids);

    }

    private static void configureParticipant(IatpParticipant participant, ParticipantRuntime runtime, Map<String, DidDocument> dids) {
        STS_RUNTIME.getContext().getService(Vault.class).storeSecret(participant.verificationId(), participant.privateKey());
        var vault = runtime.getContext().getService(Vault.class);
        var presentationRegistry = runtime.getContext().getService(PresentationCreatorRegistry.class);
        var didResolverRegistry = runtime.getContext().getService(DidResolverRegistry.class);
        var didResolver = new DidExampleResolver();
        dids.forEach(didResolver::addCached);
        didResolverRegistry.register(didResolver);

        vault.storeSecret(participant.verificationId(), participant.privateKey());
        presentationRegistry.addKeyId(participant.verificationId(), CredentialFormat.JSON_LD);
        presentationRegistry.addKeyId(participant.verificationId(), CredentialFormat.JWT);

        storeCredentials(participant, runtime);
    }

    private static void storeCredentials(IatpParticipant participant, ParticipantRuntime runtime) {
        var credentialStore = runtime.getContext().getService(CredentialStore.class);
        var jsonLd = runtime.getContext().getService(JsonLd.class);
        issueCredentials(participant, jsonLd).forEach(credentialStore::create);
    }

    private static List<VerifiableCredentialResource> issueCredentials(IatpParticipant participant, JsonLd jsonLd) {
        return List.of(
                DATASPACE_ISSUER_PARTICIPANT.issueMembershipCredential(participant, jsonLd),
                DATASPACE_ISSUER_PARTICIPANT.issueFrameworkCredential(participant, jsonLd, "PcfCredential"));
    }

    @BeforeEach
    void setup() throws IOException {
        super.setup();
    }

    @Override
    protected JsonObject createContractPolicy(String bpn) {
        return frameworkPolicy(Map.of(TX_CREDENTIAL_NAMESPACE + "Membership", "active"));
    }

}
