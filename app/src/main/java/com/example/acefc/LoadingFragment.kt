package com.example.acefc

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

class LoadingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val progressBar = ProgressBar(container?.context)
        if (container is FrameLayout) {
            val layoutParams =
                FrameLayout.LayoutParams(LOADING_WIDTH, LOADING_HEIGHT, Gravity.CENTER)
            progressBar.layoutParams = layoutParams
        }
        return progressBar
    }

    companion object {
        private val LOADING_WIDTH = 100
        private val LOADING_HEIGHT = 100
    }
}

