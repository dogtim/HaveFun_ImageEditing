This project refer to [PhotoEditor](https://github.com/burhanrashid52/PhotoEditor) and do some refine and revise the better codes,
we keep notes and can contribute in the future.

# Modify 9: Utilize the Scope Functions in Kotlin

```kotlin=
// Original
val frmBorder = rootView.findViewById<View>(R.id.editor_border)
val imgClose = rootView.findViewById<View>(R.id.image_close)
if (frmBorder != null) {
    frmBorder.setBackgroundResource(R.drawable.rounded_border_tv)
    frmBorder.tag = true
}
if (imgClose != null) {
    imgClose.visibility = View.VISIBLE
}
```
```kotlin=
// Original
rootView.findViewById<View>(R.id.editor_border)?.let {
    it.setBackgroundResource(R.drawable.rounded_border_tv)
    it.tag = true
}
rootView.findViewById<View>(R.id.image_close)?.let {
    it.visibility = View.GONE
}
```

# Modify 8: Rename method
BoxHelper.clearHelperBox()

```kotlin=
// Original
internal class BoxHelper() {
    fun clearHelperBox()
}

// Better
internal class BoxHelper() {
    fun clear()
}
```

# Modify 7: The MultiTouchListener is too big
Original includes the scale/rotate/move codes together
The events, the data strcuture should be separated to appropriate group
```kotlin=
...
// only be used to scale feature rather than move feature
private val minimumScale = 0.5f
private val maximumScale = 10.0f
...
```

# Modify 6: Deprecated APIs usage
```
// Deprecated
mGestureListener = GestureDetector(GestureListener())
// Recommend
mGestureListener = GestureDetector(context, GestureListener())
```

# Modify 5:
Duplicated members, we can take advantage of constructor to achieve the self member
```
// Original
internal class MultiTouchListener(
    deleteView: View?,
    photoEditorView: PhotoEditorView,
    photoEditImageView: ImageView?,
    private val mIsPinchScalable: Boolean,
    onPhotoEditorListener: OnPhotoEditorListener?,
    viewState: PhotoEditorViewState
) : OnTouchListener {
...
    private var onMultiTouchListener: OnMultiTouchListener? = null
    private var mOnGestureControl: OnGestureControl? = null
    private val mOnPhotoEditorListener: OnPhotoEditorListener?
    private val viewState: PhotoEditorViewState
    private val photoEditorView: PhotoEditorView
...
    init {
        // Duplicated
        this.photoEditorView = photoEditorView
    }
```

```
// After
internal class MultiTouchListener(
    deleteView: View?,
    private val photoEditorView: PhotoEditorView,
    photoEditImageView: ImageView?,
    private val mIsPinchScalable: Boolean,
    onPhotoEditorListener: OnPhotoEditorListener?,
    viewState: PhotoEditorViewState
) : OnTouchListener {
...
    private var onMultiTouchListener: OnMultiTouchListener? = null
    private var mOnGestureControl: OnGestureControl? = null
    private val mOnPhotoEditorListener: OnPhotoEditorListener?
    private val viewState: PhotoEditorViewState
...
    init {
    }
```
# Modify 4:
Code convention:
Most of the position of `init { }` are put into the end of lines
According to [class-layout](https://kotlinlang.org/docs/coding-conventions.html#class-layout)

> Class layout﻿
> The contents of a class should go in the following order:
>
> Property declarations and initializer blocks
>
> Secondary constructors
>
> Method declarations
>
> Companion object


# Modify 3:
The folder architecture, the original do not have group and design the layer to give object's duties

# Modify 2:

Original
```kotlin=
if (mEmojiListener != null) {
    mEmojiListener!!.onEmojiClick(emojisList[layoutPosition])
}
```

After
```kotlin=
listener?.onClick(shapeList[layoutPosition])
```
# Modify 1: SuppressLint("RestrictedApi")
Modify @SuppressLint("RestrictedApi")
override fun setupDialog(dialog: Dialog, style: Int) {

to onCreateDialog

Components:
1. https://developer.android.com/training/data-storage
   Media Store
   https://developer.android.com/training/data-storage/shared/media
