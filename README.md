# Maven Version Refactor Plugin

![Version](https://img.shields.io/jetbrains/plugin/v/com.mirkoalicastro.mavenversionrefactor
)

## Description

<!-- Plugin description -->
Helps to refactor a Maven <strong>pom.xml</strong> replacing inline dependencies/plugins versions with a dedicated property.

This IntelliJ plugin adds an intention action that suggests extraction of a dependency/plugin version in a POM using a property.

It adds an entry to **Preferences | Editor | Intentions | SDK Intentions**.

It is a *high priority action*, enabled when the cursor is on a non-variable dependency/plugin version (both on text and tag).

Have a look at and contribute to the GitHub [repo](https://github.com/mirkoalicastro/maven-version-refactor-plugin).
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "maven-artifacts-version-refactor"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the latest release and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

---
Plugin based on the [IntelliJ Platform Plugin Template][template].

## External resources

[Here](https://plugins.jetbrains.com/plugin/16057-maven-version-refactor) the link to the plugin homepage.

[template]: https://github.com/JetBrains/intellij-platform-plugin-template