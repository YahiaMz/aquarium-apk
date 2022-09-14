package com.aplication.aquaruim.views.fragmenets

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.aplication.aquaruim.R
import com.aplication.aquaruim.databinding.FragmentUserBinding
import com.aplication.aquaruim.models.UpdateUserModel
import com.aplication.aquaruim.utils.API_URLS
import com.aplication.aquaruim.utils.RealPathUtil
import com.aplication.aquaruim.viewmodels.AuthViewModel
import com.aplication.aquaruim.views.activities.AuthActivity
import com.aplication.aquaruim.views.customViews.SuccessToast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import me.echodev.resizer.Resizer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.regex.Pattern

@Keep class UserFragment : Fragment() {


    lateinit var updateUserBinding: FragmentUserBinding;
    val authViewModel: AuthViewModel by activityViewModels()
    val PICK_USER_IMAGE_CODE = 48;
    var imageMimeType: String? = null
    var imageRealPath: String? = null
    lateinit var sharedPreferences: SharedPreferences;
    var mPassword_Visible = false


    lateinit var initUserName: String;
    lateinit var initLastName: String;
    lateinit var initImageProfile: String;
    lateinit var initPhoneNumber: String;
    lateinit var initPassword: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.updateUserBinding = FragmentUserBinding.inflate(layoutInflater);



        initValues();
        pickImageUserProfileImage();
        showHidePassword()
        this.onUpdateClicked();
        switchToEditMode()
        logout()

        return this.updateUserBinding.root

    }



    private fun logout() {
        this.updateUserBinding.logoutBtn.setOnClickListener {
            val phoneNumber = sharedPreferences.getString("phone_number", "");
            sharedPreferences.edit().clear().commit();
            sharedPreferences.edit().putString("phone_number", phoneNumber).apply();
            Intent(activity, AuthActivity::class.java).also {
                startActivity(it);
                activity!!.finish();
            }
        }
    }


    fun switchToEditMode() {
        this.updateUserBinding.editModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            this.updateUserBinding.isEditMode = isChecked;
        }
    }

    private fun initValues() {
        this.sharedPreferences =
            requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE);

        this.initUserName = sharedPreferences.getString("name", "null").toString();
        this.initLastName = sharedPreferences.getString("lastName", "null").toString();
        this.initPhoneNumber = sharedPreferences.getString("phone_number", "null").toString()
        this.initPassword = sharedPreferences.getString("password", "null").toString()
        this.initImageProfile = sharedPreferences.getString("imageProfileUrl", "null").toString()



        this.updateUserBinding.userName = initUserName;
        this.updateUserBinding.lastName = initLastName;
        this.updateUserBinding.phoneNumber = initPhoneNumber;
        this.updateUserBinding.initPassword = initPassword;

        if (initImageProfile != "null") {
            Picasso.get().load(API_URLS.USER_PROFILE_IMAGE + initImageProfile)
                .into(updateUserBinding.updateUserImage, object : Callback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception?) {
                        updateUserBinding.updateUserImage.setImageResource(R.drawable.user_profile)
                    }

                })
        }
    }


    val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                pickImage()
            } else {
                Toast.makeText(context,
                    " permission is required to set profile image !",
                    Toast.LENGTH_SHORT).show()
            }
        }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_USER_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val imageUri = data.data;
                this.updateUserBinding.updateUserImage.setImageURI(imageUri)
                this.imageRealPath = RealPathUtil.getRealPath(requireContext(), imageUri!!);
                this.imageMimeType = getMimeType(uri = imageUri);
            }
        }

    }

    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context!!.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                .toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.lowercase(Locale.getDefault()))
        }
        return mimeType
    }

    fun pickImageUserProfileImage() {
        this.updateUserBinding.addImageTv.setOnClickListener {
            pickGalleryImage()
        }
        this.updateUserBinding.updateUserImage.setOnClickListener {
            pickImage()
        }
    }

    fun showHidePassword() {
        this.updateUserBinding.updatePasswordNumberEt.setOnTouchListener(object :
            View.OnTouchListener {
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                val ePassword = updateUserBinding.updatePasswordNumberEt

                val right: Int = 2;
                if (event?.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= ePassword.getRight() - ePassword.getCompoundDrawables()[right].getBounds()
                            .width()
                    ) {
                        if (mPassword_Visible) {

                            ePassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon_selector,
                                0,
                                R.drawable.ic_visibility_off,
                                0);
                            ePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            mPassword_Visible = false;

                        } else {
                            ePassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_icon_selector,
                                0,
                                R.drawable.ic_visibility_on,
                                0);
                            ePassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            mPassword_Visible = true;
                        }
                    }
                }
                return false;
            }

        });
    }


    fun pickGalleryImage() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                pickImage()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {

            }
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    fun pickImage() {
        val pickImageProfileIntent = Intent()
        pickImageProfileIntent.type = "image/*"
        pickImageProfileIntent.action = Intent.ACTION_PICK;
        startActivityForResult(pickImageProfileIntent, PICK_USER_IMAGE_CODE)
    }

    fun validateInputs(): Boolean {
        val nameEditText = this.updateUserBinding.updateName;
        if (nameEditText.text.length < 3) {
            nameEditText.setError("name must be at least 3 char")
            return false;
        }
        val familyNameEt = this.updateUserBinding.updateFamilyName;
        if (familyNameEt.text.length < 3) {
            familyNameEt.setError("Family name must be at least 3 char")
            return false;
        }

        val phoneNumberEt = this.updateUserBinding.UpdatePhoneNumberEt;
        val isMatch = Pattern.compile("0[5,6,7][0-9]{8}")
            .matcher(phoneNumberEt.text.toString())
            .matches();


        if (!isMatch) {
            phoneNumberEt.error = "wrong phone number !"
            return false;
        }

        val passwordEt = this.updateUserBinding.updatePasswordNumberEt
        if (passwordEt.text.length < 4) {
            passwordEt.error = "Password must be at least 4 char";
            return false;
        }

        return true;

    }

    fun onUpdateClicked() {
        this.updateUserBinding.updateUserBtn.setOnClickListener {


            if (validateInputs()) {

                val name = updateUserBinding.updateName.text.toString().trim()
                val lastName = updateUserBinding.updateFamilyName.text.toString().trim()
                val passwordS = updateUserBinding.updatePasswordNumberEt.text.toString()
                val phoneNumberS = updateUserBinding.UpdatePhoneNumberEt.text.toString()

                var imagePart: MultipartBody.Part? = null;
                if (imageMimeType != null) {
                    val image = File(imageRealPath!!);
                    val compressedImageFile: File = Resizer(requireContext())
                        .setTargetLength(1080)
                        .setQuality(80)
                        .setOutputFormat("JPEG")
                        .setOutputFilename("resized_image")
                        .setSourceImage(image)
                        .resizedFile

                    val imageToMultiPart =
                        RequestBody.create(MediaType.parse(imageMimeType), compressedImageFile);
                    imagePart =
                        MultipartBody.Part.createFormData("image", image.name, imageToMultiPart);
                }

                val namePart = RequestBody.create(MediaType.parse("multipart/-data"),
                    name);

                var lastNamePart: RequestBody? = null
                lastNamePart = RequestBody.create(MediaType.parse("multipart/-data"),
                    lastName);

                var phoneNumberPart: RequestBody? = null
                if(phoneNumberS != initPhoneNumber)
                phoneNumberPart = RequestBody.create(MediaType.parse("multipart/-data"),
                    phoneNumberS);

                var passwordPart: RequestBody? = null
                if(passwordS != initPassword)
                passwordPart = RequestBody.create(MediaType.parse("multipart/-data"),
                    passwordS);


                val sharedPreferences =
                    requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", -1)



                if (userId != -1) {

                    this.updateUserBinding.isUpdatingUser = true

                    this.authViewModel.updateUser(UpdateUserModel(userId,
                        namePart,
                        lastNamePart,
                        passwordPart,
                        phoneNumberPart,
                        imagePart)).observe(viewLifecycleOwner) {
                        if (it != null) {
                            if (it == 1) {


                                val sharedPreferences =
                                    activity!!.getSharedPreferences("USER", Context.MODE_PRIVATE)
                                val spEditor =
                                    sharedPreferences
                                        .edit();


                                if (imagePart != null) {

                                    val userImageView = this.updateUserBinding.updateUserImage;
                                    Picasso.get()
                                        .invalidate(API_URLS.USER_PROFILE_IMAGE + sharedPreferences.getString(
                                            "imageProfileUrl",
                                            null))
                                    Picasso.get()
                                        .load(API_URLS.USER_PROFILE_IMAGE + sharedPreferences.getString(
                                            "imageProfileUrl",
                                            null)).into(userImageView, object : Callback {
                                            override fun onSuccess() {
                                            }

                                            override fun onError(e: java.lang.Exception?) {
                                                userImageView.setImageResource(R.drawable.user_profile);
                                            }
                                        })
                                }

                                spEditor.putString("name", name)
                                spEditor.putString("lastName", lastName);
                                spEditor.putString("password" , passwordS);
                                spEditor.putString("phone_number" , phoneNumberS)
                                spEditor.apply();

                                SuccessToast.ShowSuccessToast("Updated with success",
                                    requireContext());

                            }
                            this.updateUserBinding.isUpdatingUser = false
                        }
                    }


                }
            }
        }
    }
}