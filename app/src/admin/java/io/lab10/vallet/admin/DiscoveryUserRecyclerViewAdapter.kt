package io.lab10.vallet.admin

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.lab10.vallet.R

import io.lab10.vallet.admin.fragments.DiscoverUsersFragment.OnListFragmentInteractionListener
import io.lab10.vallet.models.BTUsers

class DiscoveryUserRecyclerViewAdapter(private val mValues: List<BTUsers.User>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<DiscoveryUserRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].id
        holder.mContentView.text = mValues[position].name

        holder.mContentView.setOnClickListener {
            val user = holder.mItem
            if (user != null) {
                mListener?.onListFragmentInteraction(user)
            }
        }

    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: Button
        var mItem: BTUsers.User? = null

        init {
            mIdView = mView.findViewById(R.id.id) as TextView
            mContentView = mView.findViewById(R.id.content) as Button
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
