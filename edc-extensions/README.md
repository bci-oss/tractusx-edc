# EDC Extensions Overview

The following extensions provide additional functionality to the core EDC.
They are currently only available in Tractus-X EDC.

## Business Partner Validation

This extension allows for validation of business partners within the access policy.

## Control Plane EDR APIs

The goal of this extension is to simplify the process of retrieving data out of EDC.
It returns `EndpointDataReference` object, hiding all the communication details for contract offers,
contract negotiation, transfer process and retrieving the underlying data through the data-planes.

## Data Encryption

The EDC encrypts sensitive information inside a token it sends to other applications (potentially cross-company).
This extension implements the encryption of this data and should be used with secure keys and algorithms at all times.

## Data Plane Selector

This control plane extension makes it possible to configure one or more data plane instances.
During a transfer the control plane will look for an instance with matching capabilities to transfer data.

## Hashicorp Vault

This extension allows for usage of Hashicorp Vault for secret storage.
It is the default used in Tractus-X EDC.

## PostrgreSQL Migration

While the core EDC is able to interact with PostgreSQL databases,
it does not automate migrations between schema versions.
This extension adds that functionality.

## Transfer Process SFTP

This extension allows for the use of SFTP backends for the data plane (but is not included in the provided control- and data plane).

## NOTICE

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

- SPDX-License-Identifier: Apache-2.0
- SPDX-FileCopyrightText: 2021,2022,2023 Contributors to the Eclipse Foundation
- Source URL: <https://github.com/eclipse-tractusx/tractusx-edc>
