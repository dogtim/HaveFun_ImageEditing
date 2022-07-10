# Abstract
Add notes and common question.

## Where is the UITest?

We can refer to this [test](https://github.com/burhanrashid52/PhotoEditor/tree/master/app/src/androidTest/java/com/burhanrashid52/photoediting), it is amazing and a good reference let us study.

## Please describe the ScaleGestureDetector, RotateGestureDetector, and MoveGestureDetector

TODO, we should compare the components with the impelementation

## What is the difference between getX and getRawX from MotionEvent?
- [Android API](https://developer.android.com/reference/android/view/MotionEvent#getRawX())
- [The difference](https://blog.csdn.net/bzlj2912009596/article/details/75043013)

## How to debug the MotionEvent?
```
Log.d(TAG, "onTouchView() called with: event = [$event]")

// Print such below to get more detail information
onTouchView() called with: event = [MotionEvent { action=ACTION_UP, actionButton=0, id[0]=0, x[0]=208.0, y[0]=1197.0, toolType[0]=TOOL_TYPE_FINGER, buttonState=0, classification=DEEP_PRESS, metaState=0, flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0, eventTime=201484155, downTime=201483947, deviceId=3, source=0x1002, displayId=0, eventId=244965347 }]
```

## How to move multiple items which show on PhotoEditorView?

```kotlin
// Only enable dragging on focused stickers.
if (view === viewState.currentSelectedView)
// Remove this code you could achieve this goal
```