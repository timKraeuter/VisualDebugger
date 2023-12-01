# Short documentation

This file contains a short documentation of
the [visual debugger plugin](https://plugins.jetbrains.com/plugin/16851-visual-debugger).

## General

The plugin uses the IntelliJ Debugger API to listen to debugging events. The class `DebugProcessListener` listens to new
debugging sessions being started. Then it adds a session listener (`DebugSessionListener`), which is notified whenever
debugging stops. We can then access the debugging variables and create a visual representation as an object diagram. The
visualization can be embedded in the IDE using the embedded visualizer or shown in a browser using the browser
visualizer.

## Visualization

The **embedded visualizer** uses [PlantUML](https://plantuml.com/) to visualize the debugging variables. We use
the [PlantUML Smetana project](https://plantuml.com/smetana02) such that a local installation of DOT is not required.
Since PlantUML generates images, no user interaction is possible.

Consequently, we have implemented a **browser visualizer** that supports user interaction, i.e., exploring the generated object diagram similar to the exploration in the variables view in the IDE.
The browser visualization is based on the object-diagram modeler ([source](https://github.com/timKraeuter/object-diagram-js), [demo](https://timkraeuter.com/object-diagram-js/)).

## Browser visualizer

The browser visualizer makes use of WebSocket to update the connected clients/browsers whenever the debugging variables
change. An overview of the architecture is shown in the following picture.

![Architecture picture showing the plugin connecting to the browser using WebSocket](./pictures/VD-architecture.svg)

The browser initially connects to the plugin running inside IntelliJ IDEA, which leads to the creation of a WebSocket
session. Using the obtained session, the plugin will update the connected client about any changes to the debugging
variables. One can interact with the object diagram in the browser visualization by double-clicking on objects to load
their immediate children. This communication also uses the established WebSocket connection.

The user interface implementation is decoupled from the IDEA, such that it can be reused for debugging visualization,
for example, together with Eclipse IDE. The source code for the UI can be found [here](https://github.com/timKraeuter/object-diagram-modeler/tree/master/debugger).

### Visual Debugging API

The object diagram is transferred using XML conforming to the schema defined in `api/objectDiagram.xsd`.
You can find an implementation of the Visual Debugging API in the class `VisualDebuggingAPIEndpoint`.
From the point of view of the UI, the following messages are transferred.

#### Incoming WebSocket messages

The UI expects incoming messages to have a specific JSON format. It has a type (`nextDebugStep`, `loadChildren`, or `config`) and a
content which is a string conforming to the [XML schema for object diagrams](./api/objectDiagram.xsd).

```json
{
  "type": "nextDebugStep",
  "content": "XML data here...",
  "fileName": "PartsListTest",
  "line": "12"
}
```

`nextDebugStep` indicates that new debugging data is available. The UI will use the content to draw a new object diagram
discarding all previous information.

`loadChildren` is the response to a call of the UI to load more details for a given object. The response should only
contain the object and its children. The UI will merge this information with the currently shown object diagram.
The fields `fileName` and `line` are not needed.

`config` is expected after a connection has been initialized or the configuration has changed.
It contains how many debugging steps should be saved in the history of the UI using the field `savedDebugSteps` inside `content`.
Furthermore, it contains if additions and changes should be highlighted in a boolean field `coloredDiff` (see class `UIConfig`).

#### Outgoing WebSocket messages

The UI will send a plain string containing an object id to load more information for this object. The response should
use the type `loadChildren` and be structured as described above.
