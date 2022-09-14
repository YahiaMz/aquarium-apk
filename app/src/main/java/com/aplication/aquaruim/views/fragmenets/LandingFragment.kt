package com.aplication.aquaruim.views.fragmenets

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentLandingBinding


class LandingFragment : Fragment() {

    lateinit var landingBinding: FragmentLandingBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        this.landingBinding = FragmentLandingBinding.inflate(layoutInflater)
        this.landingBinding.LandingLoginButton.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, LoginFragment()).commit();
        }
        this.landingBinding.LandingSignUpButton.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction()
                .replace(R.id.authFrameLayout, SignUpFragment()).commit();
        }

        goToFbPage()
        goToInsta()
        goToTikTok()

        return this.landingBinding.root
    }

    fun goToInsta() {
        this.landingBinding.goToInstaPage.setOnClickListener {


            val uriForApp: Uri = Uri.parse("https://www.instagram.com/_u/pizzeria.aquarium.chlef/")
            val forApp = Intent(Intent.ACTION_VIEW, uriForApp)

            val uriForBrowser: Uri = Uri.parse("https://www.instagram.com/pizzeria.aquarium.chlef/")
            val forBrowser = Intent(Intent.ACTION_VIEW, uriForBrowser)

            forApp.component =
                ComponentName(
                    "com.instagram.android",
                    "com.instagram.android.activity.UrlHandlerActivity"
                )

            try {
                startActivity(forApp)
            } catch (e: ActivityNotFoundException) {
                startActivity(forBrowser)
            }
        }
    }
    fun goToFbPage() {
        this.landingBinding.goToFbPage.setOnClickListener {
            newFacebookIntent(requireActivity().packageManager,
                "https://www.facebook.com/pizzeria.aquarium.chlef/").also {
                startActivity(it);
            };
        }
    }
    fun goToTikTok( ) {
        this.landingBinding.goToTikTokPage.setOnClickListener {

            val uri: Uri = Uri.parse("https://www.tiktok.com/@pizzeria.aquarium.chlef")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            intent.setPackage("com.zhiliaoapp.musically")

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

    }

    fun newFacebookIntent(pm: PackageManager, url: String): Intent {
        var uri = Uri.parse(url)
        try {
            val applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0)
            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }
        return Intent(Intent.ACTION_VIEW, uri)
    }


}