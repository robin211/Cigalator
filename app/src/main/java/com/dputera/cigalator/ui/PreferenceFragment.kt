package com.dputera.cigalator.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.fragment_preference.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class PreferenceFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false
    companion object {
        fun newInstance(): PreferenceFragment {
            return PreferenceFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preference, container, false)
    }

    @SuppressLint("HardwareIds")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().applicationContext.getSharedPreferences(
            SP_NAME_THEME,
            Context.MODE_PRIVATE
        )

        when (sharedPreferences.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)) {
            THEME_CALMING_BLUE -> {
                check_calming_blue.visibility = View.VISIBLE
                dash_calming_blue.visibility = View.GONE

                check_dark_yet_striking.visibility = View.GONE
                dash_dark_yet_striking.visibility = View.VISIBLE

                check_knight_of_the_dark.visibility = View.GONE
                dash_knight_of_the_dark.visibility = View.VISIBLE
            }
            THEME_DARK_YET_STRIKING -> {
                check_calming_blue.visibility = View.GONE
                dash_calming_blue.visibility = View.VISIBLE

                check_dark_yet_striking.visibility = View.VISIBLE
                dash_dark_yet_striking.visibility = View.GONE

                check_knight_of_the_dark.visibility = View.GONE
                dash_knight_of_the_dark.visibility = View.VISIBLE
            }
            THEME_KNIGHT_OF_THE_DARK -> {
                check_calming_blue.visibility = View.GONE
                dash_calming_blue.visibility = View.VISIBLE

                check_dark_yet_striking.visibility = View.GONE
                dash_dark_yet_striking.visibility = View.VISIBLE

                check_knight_of_the_dark.visibility = View.VISIBLE
                dash_knight_of_the_dark.visibility = View.GONE
            }
        }

        dash_calming_blue.setOnClickListener {
            openDialog(THEME_CALMING_BLUE)
        }

        dash_dark_yet_striking.setOnClickListener {
            openDialog(THEME_DARK_YET_STRIKING)
        }

        dash_knight_of_the_dark.setOnClickListener {
            openDialog(THEME_KNIGHT_OF_THE_DARK)
        }
    }

    private fun openDialog(theme: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        when (theme) {
            THEME_CALMING_BLUE -> {
                dialog.setContentView(R.layout.dialog_calming_blue)
                val yesBtn = dialog.findViewById(R.id.btn_ok) as TransitionButton
                yesBtn.setOnClickListener {
                    sharedPreferences.edit().putInt(SP_CURRENT_THEME, THEME_CALMING_BLUE).apply()
                    dialog.dismiss()
                    loadAd()
                }
            }
            THEME_DARK_YET_STRIKING -> {
                dialog.setContentView(R.layout.dialog_dark_yet_striking)
                val yesBtn = dialog.findViewById(R.id.btn_ok) as TransitionButton
                yesBtn.setOnClickListener {
                    sharedPreferences.edit().putInt(SP_CURRENT_THEME, THEME_DARK_YET_STRIKING).apply()
                    dialog.dismiss()
                    loadAd()
                }
            }
            THEME_KNIGHT_OF_THE_DARK -> {
                dialog.setContentView(R.layout.dialog_knight_of_the_dark)
                val yesBtn = dialog.findViewById(R.id.btn_ok) as TransitionButton
                yesBtn.setOnClickListener {
                    sharedPreferences.edit().putInt(SP_CURRENT_THEME, THEME_KNIGHT_OF_THE_DARK).apply()
                    dialog.dismiss()
                    loadAd()
                }
            }
        }
        dialog.show()
    }

    private fun loadAd() {
        (activity as OptionActivity?)!!.showLoading()
        val adRequest = AdRequest.Builder().build()
        InterstitialAd
            .load(
                requireContext(), AD_UNIT_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        (activity as OptionActivity?)!!.hideLoading()
                        Log.d("Preference", adError.message)
                        mInterstitialAd = null
                        mAdIsLoading = false
                        requireActivity().recreate()
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        Log.d("Preference", "Ad was loaded.")
                        (activity as OptionActivity?)!!.hideLoading()
                        mInterstitialAd = interstitialAd
                        mAdIsLoading = false
                        showInterstitial()
                    }

                }
            )

    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Ad", "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
//                    loadAd()
                    requireActivity().recreate()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d("Ad", "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    requireActivity().recreate()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("Ad", "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(requireActivity())
        } else {
            Log.d("Ad", "Ad wasn't loaded.")
        }
    }

}