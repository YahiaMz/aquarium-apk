package com.aplication.aquaruim.views.fragmenets

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.aplication.aquaruim.databinding.FragmentCompleteRegisterBinding
import com.aplication.aquaruim.models.UpdateUserModel
import com.aplication.aquaruim.utils.RealPathUtil
import com.aplication.aquaruim.viewmodels.AuthViewModel
import com.aplication.aquaruim.views.activities.MainActivity
import me.echodev.resizer.Resizer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*


@Keep class CompleteRegisterFragment : Fragment() {

    lateinit var completeRegisterBinding: FragmentCompleteRegisterBinding;
    val authViewModel: AuthViewModel by activityViewModels()
    val PICK_USER_IMAGE_CODE = 7;
    var imageMimeType: String? = null
    var imageRealPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        this.completeRegisterBinding = FragmentCompleteRegisterBinding.inflate(layoutInflater);
        pickImageUserProfileImage();
        this.onCompleteRegisterClicked();
        return this.completeRegisterBinding.root
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 7 && resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                val imageUri = data.data!!;


                this.completeRegisterBinding.addUserImage.setImageURI(imageUri)
                this.imageRealPath = RealPathUtil.getRealPath(requireContext(), imageUri);
                this.imageMimeType = getMimeType(uri = imageUri);
            }


        }

    }
    fun reduceImageSize(file: File): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // The new size we want to scale to
            val REQUIRED_SIZE = 75

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            selectedBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            file
        } catch (e: Exception) {
            Log.d("REDUCED IMAGE", e.message.toString())
            null
        }
    }



    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context!!.getContentResolver()
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
        this.completeRegisterBinding.addUserImage.setOnClickListener {
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
    }


    fun pickImage() {
        val pickImageProfileIntent = Intent()
        pickImageProfileIntent.type = "image/*"
        pickImageProfileIntent.action = Intent.ACTION_PICK;
        startActivityForResult(pickImageProfileIntent, PICK_USER_IMAGE_CODE)
    }

    fun validateInputs(): Boolean {
        val nameEditText = this.completeRegisterBinding.enterName;
        if (nameEditText.text.length < 3) {
            nameEditText.setError("name must be at least 3 char")
            return false;
        }
        val familyNameEt = this.completeRegisterBinding.enterFamilyName;
        if (familyNameEt.text.length < 3) {
            familyNameEt.setError("Family name must be at least 3 char")
            return false;
        }
        return true;

    }

    fun onCompleteRegisterClicked() {
        this.completeRegisterBinding.completeRegister.setOnClickListener {
            if (validateInputs()) {


                val name = completeRegisterBinding.enterName.text.toString().trim()
                val lastName = completeRegisterBinding.enterFamilyName.text.toString().trim()


                this.completeRegisterBinding.isCompleting = true
                var imagePart: MultipartBody.Part? = null;
                if (imageMimeType != null) {
                    val image = File(imageRealPath!!);


                    val compressedImageFile: File = Resizer(requireContext())
                        .setTargetLength(1080)
                        .setQuality(80)
                        .setOutputFormat("JPEG")
                        .setOutputFilename("resized_image")
                        .setSourceImage(image)
                        .getResizedFile()


                    val imageToMultiPart =
                        RequestBody.create(MediaType.parse(imageMimeType), compressedImageFile);
                    imagePart =
                        MultipartBody.Part.createFormData("image", image.name, imageToMultiPart);


                }


                val namePart = RequestBody.create(MediaType.parse("multipart/-data"),
                    name);

                val lastNamePart = RequestBody.create(MediaType.parse("multipart/-data"),
                    lastName);


                val sharedPreferences =
                    requireContext().getSharedPreferences("USER", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getInt("user_id", -1)
                if (userId != -1) {

                    this.authViewModel.updateUser(UpdateUserModel(userId,
                        namePart,
                        lastNamePart,
                        null,
                        null,
                        imagePart)).observe(viewLifecycleOwner) {
                        if (it != null) {
                            if (it == 1) {

                                val spEditor =
                                    context!!.getSharedPreferences("USER", Context.MODE_PRIVATE)
                                        .edit();

                                spEditor.putString("name" , name)
                                spEditor.putString("lastName" , lastName)

                                spEditor.apply()
                                Intent(requireContext(),
                                    MainActivity::class.java).also { mainActivity ->
                                    startActivity(mainActivity);
                                    activity!!.finish();
                                }
                            }
                            this.completeRegisterBinding.isCompleting = false

                        }
                    }


                }
            }
        }
    }
}