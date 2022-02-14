package com.dputera.cigalator.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.dputera.cigalator.db.CigaretteTimeDB
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class SettingFragment : Fragment() {
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: CigaretteTimeDB
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false
    companion object {
        fun newInstance(): SettingFragment {
            return SettingFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().applicationContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        btn_change_program.setOnClickListener {
            showDialog()
        }

        btn_change_counter.setOnClickListener {
            goToChangeCounter(sharedPreferences.getInt(SP_PROGRAM_TYPE, 1))
        }
    }

    private fun goToChooseProgram() {
        db = CigaretteTimeDB.getInstance(requireActivity())
        sharedPreferences.edit().clear().apply()
        GlobalScope.launch {
            db.cigaretteTimeDao().deleteAllData()
        }

        val intent = Intent(requireContext(), ChooseProgramActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun goToChangeCounter(program : Int) {
        val intent : Intent = if (program == PROGRAM_TYPE_RELAXED){
            Intent(requireContext(), SetUpRelaxedActivity::class.java)
        }else{
            Intent(requireContext(), SetUpTargetedActivity::class.java)
        }
        startActivity(intent)
    }

    private fun showDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_are_you_sure)
        val yesBtn = dialog.findViewById(R.id.btn_ok) as TransitionButton
        yesBtn.setOnClickListener {
            dialog.dismiss()
            loadAd()
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
                        goToChooseProgram()
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        (activity as OptionActivity?)!!.hideLoading()
                        Log.d("Preference", "Ad was loaded.")
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
                    goToChooseProgram()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d("Ad", "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    goToChooseProgram()
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