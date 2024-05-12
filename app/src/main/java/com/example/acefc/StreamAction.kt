package com.example.acefc

import androidx.leanback.widget.Action


class StreamAction(id: String, label1: CharSequence, label2: CharSequence) :
    Action(0, label1, label2) {
        val contentId: String = id
}