package io.lab10.vallet

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import io.lab10.vallet.models.Vouchers

class VoucherAdapter(private val myDataset: MutableList<Vouchers.Voucher>) :
        RecyclerView.Adapter<VoucherAdapter.ViewHolder>() {

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mVoucherName: TextView
        val mVoucherBalance: TextView
        var mItem: Vouchers? = null

        init {
            mVoucherName = mView.findViewById(R.id.voucherName) as TextView
            mVoucherBalance = mView.findViewById(R.id.voucherBalance) as TextView
        }

        override fun toString(): String {
            return super.toString() + " '" + mVoucherBalance.text + "'"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): VoucherAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.voucher_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mVoucherBalance.text = myDataset[position].balance.toString()
        holder.mVoucherName.text = myDataset[position].name
    }

    override fun getItemCount() = myDataset.size
}