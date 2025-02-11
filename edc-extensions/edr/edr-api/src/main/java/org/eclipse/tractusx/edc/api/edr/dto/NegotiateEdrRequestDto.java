/*
 *  Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.edc.api.edr.dto;

import org.eclipse.edc.connector.api.management.contractnegotiation.model.ContractOfferDescription;
import org.eclipse.edc.spi.types.domain.callback.CallbackAddress;

import java.util.ArrayList;
import java.util.List;

import static org.eclipse.edc.spi.CoreConstants.EDC_NAMESPACE;
import static org.eclipse.tractusx.edc.edr.spi.CoreConstants.TX_NAMESPACE;

public class NegotiateEdrRequestDto {

    public static final String EDR_REQUEST_SIMPLE_DTO_TYPE = "NegotiateEdrRequestDto";
    public static final String EDR_REQUEST_DTO_TYPE = TX_NAMESPACE + EDR_REQUEST_SIMPLE_DTO_TYPE;
    public static final String EDR_REQUEST_DTO_COUNTERPARTY_ADDRESS = EDC_NAMESPACE + "counterPartyAddress";
    public static final String EDR_REQUEST_DTO_PROTOCOL = EDC_NAMESPACE + "protocol";
    public static final String EDR_REQUEST_DTO_COUNTERPARTY_ID = EDC_NAMESPACE + "counterPartyId";
    public static final String EDR_REQUEST_DTO_PROVIDER_ID = EDC_NAMESPACE + "providerId";
    public static final String EDR_REQUEST_DTO_OFFER = EDC_NAMESPACE + "offer";
    public static final String EDR_REQUEST_DTO_CALLBACK_ADDRESSES = EDC_NAMESPACE + "callbackAddresses";

    private String counterPartyAddress;
    private String protocol = "ids-multipart";
    private String counterPartyId;

    private String providerId;

    private ContractOfferDescription offer;
    private List<CallbackAddress> callbackAddresses = new ArrayList<>();

    private NegotiateEdrRequestDto() {

    }

    public String getCounterPartyAddress() {
        return counterPartyAddress;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public String getProviderId() {
        return providerId;
    }

    public List<CallbackAddress> getCallbackAddresses() {
        return callbackAddresses;
    }

    public ContractOfferDescription getOffer() {
        return offer;
    }

    public static final class Builder {
        private final NegotiateEdrRequestDto dto;

        private Builder() {
            dto = new NegotiateEdrRequestDto();
        }

        public Builder connectorAddress(String connectorAddress) {
            dto.counterPartyAddress = connectorAddress;
            return this;
        }

        public Builder protocol(String protocol) {
            dto.protocol = protocol;
            return this;
        }

        public Builder counterPartyId(String connectorId) {
            dto.counterPartyId = connectorId;
            return this;
        }

        public Builder offer(ContractOfferDescription offer) {
            dto.offer = offer;
            return this;
        }

        public Builder providerId(String providerId) {
            dto.providerId = providerId;
            return this;
        }

        public Builder callbackAddresses(List<CallbackAddress> callbackAddresses) {
            dto.callbackAddresses = callbackAddresses;
            return this;
        }

        public NegotiateEdrRequestDto build() {
            return dto;
        }

        public static Builder newInstance() {
            return new Builder();
        }
    }
}
