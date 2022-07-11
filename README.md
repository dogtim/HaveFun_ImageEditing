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
- Please check the SDK path of local.properties is set to correct. (It might not be changed as the IDE sometime wrong)
    - Android-Studio -> Preferences -> Android SDK Location


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

It is very different from below common use cases in [MVVM](https://github.com/android/architecture-samples/tree/todo-mvvm-databinding)

> Gesture -> VIEW(Click request) -> ViewModel -> Repository -> (Remote or Local)



### Components
Please demonstrate the OOP principles and component design in ways you are familiar with. You can use any of the architecture including but not limited to Android MVVM.
The whole sketch of class and components will update [here](https://app.diagrams.net/#G1z8-ujAls_4BCl_PveP2SQTaGk8Po57jR)

The key class is PhotoEditor, it provides most of entry point when user operate the items on the `PhotoEditorView`
```kotlin
class PhotoEditor (
    private val photoEditorView: PhotoEditorView
)
// To keep the stack and arraylist of operated Graphic
private val viewState: PhotoEditorViewState = PhotoEditorViewState()
// The implementation of addView/removeView/undo/redo
private val graphicManager: GraphicManager = GraphicManager(photoEditorView, viewState)
// Clear the layout
private val boxHelper: BoxHelper = BoxHelper(photoEditorView, viewState)
```

`PhotoEditorView`
- It is a RelativeLayout and any user actions such as adding or moving view operating on this layout
- Output file by this view's bitmap
    - PhotoSaverTask.captureView

#### How to add Emoji and Photo?
The relationships between PhotoEditor & (Emoji/Photo)
```kotlin
// To define the common interface, such as setup listener
internal abstract class Graphic(...)

internal class Emoji(...) : Graphic
internal class PhotoImage(...) : Graphic

val photoEditor = PhotoEditor(..)
// Add a Emoji into PhotoEditorView with setup listener or configuration
photoEditor.addEmoji()
// Add a addImage into PhotoEditorView with setup listener or configuration
photoEditor.addImage()
```

Emoji's data comes from
```kotlin
ShapeAdapter() {
    private val shapeList: ArrayList<String> by lazy {
        ...
        val emojiList = context.resources.getStringArray(R.array.photo_editor_emoji)
        ...
    }
}
```

photo data comes from other App such as google photo
```kotlin
private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let {
            photoEditor?.addImage(it)
        }
    }
}
```

#### How to handle the undo/redo?
```kotlin
// Data structure in PhotoEditorViewState class
class PhotoEditorViewState {
    var currentSelectedView: View? = null
    private val addedViews: MutableList<View>
    private val redoViews: Stack<View>
    ...
}

// The operator is GraphicManager
internal class GraphicManager {
    fun undoView(): Boolean
    fun redoView(): Boolean
}

```

#### How to remove Graphic such as Emoji & PhotoImage?
They can remove its self by adding specified ImageView
```kotlin
internal abstract class Graphic {
    private fun setupRemoveView(rootView: View) {
        rootView.findViewById<ImageView>(R.id.image_close)?.setOnClickListener {
            graphicManager?.removeView(this@Graphic)
        }
    }
}
```

#### How to scale & move the graphic?
```kotlin
// Set the below listener to Graphic
internal class MultiTouchListener {
    override fun onTouch(view: View, event: MotionEvent): Boolean {
        // The complexity implementation, but if you meet some troubles on interaction, 
        // you may find the buggy from this block, it might be the most of root cause
    }
}
```

#### How to export the decorated image?
```kotlin=
// Set the below listener to Graphic
class MainActivity {
    // Step 1: Create the URI of Image File
    // Step 2: Generate the image file and save to this URI
    private fun saveImage() {
        ....
    }
}
```

# Reference
- [BPCollage](https://github.com/chemickypes/BPCollage)
- [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor)
