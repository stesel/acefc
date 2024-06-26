package com.example.acefc

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition


/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class VideoDetailsFragment : DetailsSupportFragment() {

    private var mSelectedLiveFC: LiveFC? = null

    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate DetailsFragment")
        super.onCreate(savedInstanceState)

        mSelectedLiveFC = activity!!.intent.getSerializableExtra(DetailsActivity.LIVE_FC) as LiveFC
        if (mSelectedLiveFC != null) {
            mPresenterSelector = ClassPresenterSelector()
            mAdapter = ArrayObjectAdapter(mPresenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
            adapter = mAdapter
            onItemViewClickedListener = ItemViewClickedListener()
        } else {
            val intent = Intent(context!!, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupDetailsOverviewRow() {
        Log.d(TAG, "doInBackground: " + mSelectedLiveFC?.toString())
        val row = DetailsOverviewRow(mSelectedLiveFC)
        row.imageDrawable = ContextCompat.getDrawable(context!!, R.drawable.bg)
        val width = convertDpToPixel(context!!, DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(context!!, DETAIL_THUMB_HEIGHT)
        Glide.with(context!!)
            .load(R.drawable.card)
            .centerCrop()
            .into<SimpleTarget<Drawable>>(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    drawable: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    Log.d(TAG, "details overview card image url ready: " + drawable)
                    row.imageDrawable = drawable
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })

        mAdapter.add(row)

        if (mSelectedLiveFC != null) {
            val mLoadingFragment = LoadingFragment()

            activity!!.supportFragmentManager
                .beginTransaction()
                .add(R.id.details_fragment, mLoadingFragment)
                .commit()

            DataProvider.getLiveFCStreams(mSelectedLiveFC!!.id) { liveFCStreams ->

                val actionAdapter = ArrayObjectAdapter()

                for (item in liveFCStreams) {
                    actionAdapter.add(
                        StreamAction(
                            item.id,
                            "${item.quality} KB/s",
                            "Language: ${item.language}"
                        )
                    )
                }

                if (activity != null && row != null) {
                    activity!!.supportFragmentManager
                        .beginTransaction()
                        .remove(mLoadingFragment)
                        .commit()

                    row.actionsAdapter = actionAdapter
                }
            }
        }
    }

    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor =
            ContextCompat.getColor(context!!, R.color.selected_background)

        // Hook up transition element.
        val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
        sharedElementHelper.setSharedElementEnterTransition(
            activity, DetailsActivity.SHARED_ELEMENT_NAME
        )
        detailsPresenter.setListener(sharedElementHelper)
        detailsPresenter.isParticipatingEntranceTransition = true

        detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
            val streamAction = action as StreamAction
            val intent = Intent("org.acestream.action.start_content")
            intent.setData(Uri.parse("acestream:?content_id=${streamAction.contentId}"))
            intent.putExtra("org.acestream.EXTRA_SELECTED_PLAYER", "{\"type\": 3}")
            startActivity(intent)
        }
        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }

    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder?,
            item: Any?,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            if (item is LiveFC) {
                Log.d(TAG, "Item: " + item.toString())
                val intent = Intent(context!!, DetailsActivity::class.java)
                intent.putExtra(resources.getString(R.string.live_fc), mSelectedLiveFC)

                val bundle =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        activity!!,
                        (itemViewHolder?.view as ImageCardView).mainImageView,
                        DetailsActivity.SHARED_ELEMENT_NAME
                    )
                        .toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    companion object {
        private val TAG = "VideoDetailsFragment"

        private val DETAIL_THUMB_WIDTH = 274
        private val DETAIL_THUMB_HEIGHT = 274
    }
}