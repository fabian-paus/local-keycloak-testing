# Local Keycloak Testing

| :warning: Prototyping Phase                                                                                         |
|:--------------------------------------------------------------------------------------------------------------------|
| The code in this repository is currently being prototyped. It is not recommended to use for any real projects yet.  |

## Goal

This project aims to simplify automated testing for the identity and access management software 
[Keycloak](https://www.keycloak.org/).

It should support both the development and operation by providing support in the following areas:
- Integration tests for Keycloak extensions
- User interface (UI) tests for Keycloak themes
- Infrastructure-as-code configuration for reproducible deployments
- Monitoring for the core authentication services

I want to provide tools that can integrate in the common development environments of Keycloak developers.
This means that these tools should be built upon and seamlessly integrate into the [Java](https://www.java.com/), [Maven](https://maven.apache.org/) and [Keycloak](https://www.keycloak.org/) ecosystem.

## Progress

The project is currently in an early prototyping phase. 
Therefore, this code is not production ready yet.
However, some core ideas are currently being explored.

| Component         | State          | Comment                                                |
|-------------------|----------------|--------------------------------------------------------|
| Integration Tests | :construction: | Starting a local Keycloak instance (under development) |
| UI Tests          | :hourglass:    | Investigating UI testing frameworks                    |
| Configuration     | :hourglass:    | Researching how to tackle this problem                 |
| Monitoring        | :hourglass:    | Defining what needs to be monitored                    | 

## Related Projects

Other projects have already worked on related problems:

- [Keycloak Testcontainer](https://github.com/dasniko/testcontainers-keycloak): Allows to start a Keycloak instance in a Docker container using the [Testcontainers](https://testcontainers.com/) framework. 
In contrast, I want to provide a way to start a local Keycloak instance using only Java native tools, i.e. without the need to setup a Docker daemon. This will allow easier integration into CI/CD pipelines, since they already run on Docker (Testcontainers require a complicated Docker-in-Docker setup).
- [terraform-provider-keycloak](https://github.com/keycloak/terraform-provider-keycloak): Enables the setup and configuration of a Keycloak instance using [Terraform](https://www.terraform.io/).
The configuration is done via text files. 
I want to provide a way to define the Keycloak configuration in familiar Java code, which is easier to write, read and debug for most Keycloak developers.
- [Playwright](https://playwright.dev/): A general purpose UI testing framework for websites. 
I am currently investigating whether this testing framework could be used to test customized Keycloak UI.

