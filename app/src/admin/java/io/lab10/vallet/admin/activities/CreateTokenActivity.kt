package io.lab10.vallet.admin.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.lab10.vallet.R

import kotlinx.android.synthetic.admin.activity_voucher.*
import android.widget.Toast
import kotlinx.android.synthetic.admin.fragment_voucher_name.*
import kotlinx.android.synthetic.admin.fragment_voucher_name.view.*
import android.view.WindowManager
import io.lab10.vallet.models.Tokens
import android.net.ConnectivityManager




class CreateTokenActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mStepsPagerAdapter: StepsPagerAdapter? = null
    var voucherName: String = "ATS"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voucher)

        mStepsPagerAdapter = StepsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the step adapter.
        voucherSettingsViewPager.adapter = mStepsPagerAdapter
        voucherSettingsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                // TODO nothing to do here since we have just one page at the moment
            }

        })
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the steps.
     */
    inner class StepsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 -> {
                    return VoucherNameFragment.newInstance(voucherSettingsViewPager)
                } else -> {
                    return VoucherNameFragment.newInstance(voucherSettingsViewPager)
                }
            }
        }

        override fun getCount(): Int {
            return 1
        }
    }

    class VoucherNameFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {

            val rootView = inflater.inflate(R.layout.fragment_voucher_name, viewGroup, false)
            val sharedPref = activity.getSharedPreferences("voucher_pref", Context.MODE_PRIVATE)
            val voucherName = sharedPref.getString(resources.getString(R.string.shared_pref_voucher_name), "")
            rootView.inputVoucherName.setText(voucherName)

            val finishButton = rootView.getStarterd
            finishButton.setOnClickListener() { v ->

                if (haveNetworkConnection()) {
                    // TODO add voucher.valid? before submitting

                    // TOOD this seems not work very fast. There is a lag between pressed and showing progress bar.
                    // Replace that with event and do stuff in background
                    activity.runOnUiThread {

                        progressBar.visibility = View.VISIBLE
                        activity.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                    val voucherName = inputVoucherName.text.toString()
                    val voucherDecimal = 12;

                    val sharedPref = activity.getSharedPreferences("voucher_pref", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    var voucherType = Tokens.Type.EUR.toString()

                    // TODO: Manage password for the key
                    val walletFile = Web3jManager.INSTANCE.createWallet(context, "123")
                    val walletAddress = Web3jManager.INSTANCE.getWalletAddress(walletFile)
                    editor.putString(context.resources.getString(R.string.shared_pref_voucher_wallet_file), walletFile)
                    editor.putString(context.resources.getString(R.string.shared_pref_voucher_wallet_address), walletAddress)
                    editor.commit()

                    // TODO trigger that only if balance is lower then needed amount for creating transaction.
                    //

                    if (true) { // TOOD Check for balance if 0 request funds and create new token if balance is positive generate only new token
                        FaucetManager.INSTANCE.getFoundsAndGenerateNewToken(context, walletAddress, voucherName, voucherType, voucherDecimal)
                    } else {
                        Web3jManager.INSTANCE.generateNewToken(context, voucherName, voucherType, voucherDecimal)
                    }

                    val intent = Intent(view?.context, AdminActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(activity, "Pleaes connect to the internet to continue", Toast.LENGTH_LONG).show()
                }
            }

            return rootView
        }

        private fun haveNetworkConnection(): Boolean {
            var haveConnectedWifi = false
            var haveConnectedMobile = false

            val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.allNetworkInfo
            for (ni in netInfo) {
                if (ni.typeName.equals("WIFI", ignoreCase = true))
                    if (ni.isConnected)
                        haveConnectedWifi = true
                if (ni.typeName.equals("MOBILE", ignoreCase = true))
                    if (ni.isConnected)
                        haveConnectedMobile = true
            }
            return haveConnectedWifi || haveConnectedMobile
        }

        companion object {
            var voucherViewPager: ViewPager? = null

            fun newInstance(viewPager: ViewPager): VoucherNameFragment {
                val fragment = VoucherNameFragment()
                voucherViewPager = viewPager
                return fragment
            }
        }
    }
}