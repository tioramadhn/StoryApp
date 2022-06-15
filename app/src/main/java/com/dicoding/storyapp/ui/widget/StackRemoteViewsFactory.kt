package com.dicoding.storyapp.ui.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.local.UserPreference
import com.dicoding.storyapp.utils.uriToBitmap


internal class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private var listItem = ArrayList<String>()

    override fun onDataSetChanged() {
        val mUserPreference = UserPreference(mContext)
        val user = mUserPreference.getUser()
        if (user.isLogin) {
            val data = mUserPreference.getImage()
            data?.map {
                listItem.add(it.value.toString())
            }
        }else{
            listItem.clear()
        }
        Log.d("StackRemote", "data size: ${listItem.size}")
    }


    override fun onCreate() {

    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = listItem.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        rv.setImageViewBitmap(R.id.imageView_stack, uriToBitmap(listItem[position]))
        val extras = bundleOf(
            StoryAppWidget.EXTRA_ITEM to position
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)
        rv.setOnClickFillInIntent(R.id.imageView_stack, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}