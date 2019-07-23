<div align="center">
  <img src="https://github.com/JDevZone/CheckableTextView/blob/master/logo.png" alt="" width="70px" height="70px">
</div>

<h3 align="center">CheckableTextView</h3>


--------------
<a href="https://github.com/JDevZone/CheckableTextView">
<img align="left" src="https://github.com/JDevZone/CheckableTextView/blob/master/sample.gif" width="400" height="180" /></a>

<p><h1 align="left">Checkable TextView [KOTLIN]</h1></p>

<h4>:zap:A simple and flexible Checked TextView or Checkable TextView written in Kotlin:zap:</h4>



[![](https://jitpack.io/v/JDevZone/CheckableTextView.svg)](https://jitpack.io/#JDevZone/CheckableTextView)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Checkable%20TextView-orange.svg?style=flat)](https://android-arsenal.com/details/1/7770)
[![GitHub license](https://img.shields.io/github/license/JDevZone/CheckableTextView.svg?style=flat)](https://github.com/JDevZone/CheckableTextView/blob/master/LICENSE)

---------------------------
### Installation

1. Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```


2. Add the dependency in app gradle

```groovy
	dependencies {
	        implementation 'com.github.JDevZone:CheckableTextView:{latest_version}'
	}
```
### Basic usage

```xml
<com.devzone.checkabletextview.CheckableTextView
            android:layout_marginTop="20dp"
            android:background="#e8e8e8"
            app:ctv_TextStyle="@style/TextAppearance.General"
            app:ctv_IconTint="@color/colorAccent"
            app:ctv_IconChecked="true"
            app:ctv_Icon="@drawable/ic_cancel_custom_vector"
            app:ctv_Text="@string/app_name"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>
``` 
### Change State Programatically

You can change checked state as follows :
```kotlin
checkedTV.setChecked(isChecked)
```
Default value `shouldNotifyListeners` is false

***or***

```kotlin
checkedTV.setChecked(isChecked,shouldNotifyListeners)
```
 First Boolean parameter `isChecked` sets the current state
 Second Boolean parameter `shouldNotifyListeners` determines
 if `onCheckChange` method should be called or not.
 
### Get Current State
```kotlin
checkedTV.isChecked()
```
No Fancy enums, Just true for checked state and false for unchecked is returned

### Listen State Changes

You can listen to state changes by registering a listener like as follows :
```kotlin
checkedTV.setOnCheckChangeListener()
```
and get callback in implemented method :
```kotlin
override fun onCheckChange(view: View, isChecked: Boolean) {
        // checkedTV returned as view
        // isChecked current state of the view
    }
```

### Experimental

```kotlin
checkedTV.setClickEnabled(isClickable: Boolean)
```
Pass `isClickable` true for enable and false for disable clicks

> currently restricted to `RestrictTo.Scope.LIBRARY`
> you can use it simply Suppressing Lint Warnings (if have any) as `@SuppressLint("RestrictedApi")`

### Customisation

Here are the attributes you can specify through XML or related setters:
* `ctv_Text` - Set text.
* `ctv_TextSize` - Set text size.
* `ctv_TextColor` - Set text color.
* `ctv_TextStyle` - Set text style.
* `ctv_Icon` - Set custom icon.
* `ctv_IconTint` - Set icon tint.
* `ctv_IconChecked` - Set textView state checked.

### 📄 License

Checkable TextView is released under the MIT license.
See [LICENSE](./LICENSE) for details.


          
