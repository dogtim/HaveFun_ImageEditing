# Have Fun Image Editing

# Abstract
Users can edit the objects which are images or shapes, such as add/delete/resize operations on Canvas. This project is inspired from - [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor)

There are two important documents to me are
1. QUESTION.md
  - The notes help me to figure out the obfuscate & confusion about some components. As starting the thinking, you have the conscious to deep dive more
2. REFINEMENT.md
  - The notes keep the refinement and better coding habbit should use to. It is not a critism for the original project, as a developer, we should always find better way and consice, clean way to move on.

# Prerequisites
- Android Studio Chipmunk | 2021.2.1

# Features
1. Add new object in the center of the canvas
2. Delete any of objects onto canvas
3. Resize any of the objects
4. Drag the selecting shape around
5. Can undo and redo, user could undo deleting or adding object
6. Export and share as images.

# Architecture & Philosophy
## Idea
The simple Photo editing project differs from other browsing apps that get data from remote storage. Here is a list that demos the [MVVM/MVC/MVP](https://github.com/android/architecture-samples) from Google's Open Source project. You can checkout to different branches and find out the appropriate architecture pattern.

To start the project by the old one, the very traditional architecture

### MVC
No matter the architecture you pick up, you should follow the S.O.L.I.D pattern. This is more important than pursuing the "best" architecture pattern.

### WHY?
In photo editing, there is a trick thought inside the design. The VIEW is sometimes the DATA, and vice versa. We operate the VIEW to form a DATA and then generate the new VIEW.

For example:
The user can move the VIEW(Shape or PhotoImage) on the canvas. The VIEW does not change any appearance but the position only, and its positions (x and y) held by this VIEW

> Gesture -> VIEW -> VIEW(Internal Data, such as x, y)

It is very different from the common use cases in MVVM

> Gesture -> VIEW(Click request) -> ViewModel -> Repository -> (Remote or Local)

### Components
Please demonstrate the OOP principles and component design in ways you are familiar with. You can use any of the architecture including but not limited to Android MVVM.
The whole sketch of class and components will update [here](https://app.diagrams.net/#G1z8-ujAls_4BCl_PveP2SQTaGk8Po57jR)



# Reference
- [BPCollage](https://github.com/chemickypes/BPCollage)
- [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor)