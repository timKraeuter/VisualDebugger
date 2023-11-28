![Test & Analyze](https://github.com/timKraeuter/VisualDebugger/workflows/Build/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=alert_status)](https://sonarcloud.io/dashboard?id=timKraeuter_VisualDebugger)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=ncloc)](https://sonarcloud.io/dashboard?id=timKraeuter_VisualDebugger)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=coverage)](https://sonarcloud.io/dashboard?id=timKraeuter_VisualDebugger)
 [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=bugs)](https://sonarcloud.io/summary/new_code?id=timKraeuter_VisualDebugger)
 [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=timKraeuter_VisualDebugger)
 [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=timKraeuter_VisualDebugger&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=timKraeuter_VisualDebugger)

# Visual Debugger IntelliJ Plugin

Repository for the [visual debugger IntelliJ plugin](https://plugins.jetbrains.com/plugin/16851-visual-debugger).
- A short documentation of the plugin can be found [here](./documentation/README.md).
- The browser-based UI for the plugin can be found [here](https://github.com/timKraeuter/object-diagram-modeler/tree/master/debugger).

A preprint of my *research paper* about the visual debugger can be found [here](https://github.com/timKraeuter/ICSME-2022/blob/main/visual-debugger.pdf).

# Features

1. The debugger **highlights changes** by computing a diff using [object-diagram-js-differ](https://github.com/timKraeuter/object-diagram-js-differ):

   ![PNG showing a diff](https://github.com/timKraeuter/object-diagram-js/blob/master/documentation/diff.png)

3. The debugger saves the **debugging history** such that a user can step back in the UI:

   ![Gif showing the history feature](https://github.com/timKraeuter/object-diagram-js/blob/master/documentation/steps.gif)

# Citation
[![DOI](https://zenodo.org/badge/359495483.svg)](https://zenodo.org/doi/10.5281/zenodo.10018177)

You can cite the visual debugger tool in your papers using the DOI above.
