# maven-artifacts-version-refactor

<!-- Plugin description -->
This IntelliJ plugin adds an intention action that suggests extraction of a dependency version in a pom using a property.

It adds an entry to **Preferences | Editor | Intentions | SDK Intentions**.

It is a *high priority action*, enabled when the cursor is on a non-variable dependency version (both on text and tag).
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

[template]: https://github.com/JetBrains/intellij-platform-plugin-template