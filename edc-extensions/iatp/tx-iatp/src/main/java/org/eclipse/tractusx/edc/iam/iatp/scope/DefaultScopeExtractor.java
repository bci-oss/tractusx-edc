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

package org.eclipse.tractusx.edc.iam.iatp.scope;

import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.TokenParameters;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

import static java.lang.String.format;

/**
 * Extract for TX default scopes e.g. MembershipCredential scope
 */
public record DefaultScopeExtractor(Set<String> defaultScopes) implements BiFunction<Policy, PolicyContext, Boolean> {

    @Override
    public Boolean apply(Policy policy, PolicyContext policyContext) {
        var tokenBuilder = policyContext.getContextData(TokenParameters.Builder.class);
        if (tokenBuilder == null) {
            throw new EdcException(format("%s not set in policy context", TokenParameters.Builder.class.getName()));
        }

        var tokenParam = tokenBuilder.build();
        var existingScope = tokenParam.getStringClaim("scope");
        var newScopes = new HashSet<>(defaultScopes);
        newScopes.add(existingScope);
        tokenBuilder.claims("scope", String.join(" ", newScopes).trim());
        return true;
    }
}
