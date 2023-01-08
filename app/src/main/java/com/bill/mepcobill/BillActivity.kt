package com.bill.mepcobill

import android.app.Dialog
import android.app.ProgressDialog
import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.custom_dialog.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*

class BillActivity : AppCompatActivity() {


    val context=this
    var connectivity: ConnectivityManager?=null
    var info : NetworkInfo?=null
    var dialog: ProgressDialog? = null


    var name1:String ?=null
    var due_date1:String ?=null
    var Payable_Within_Due_Date:String ?=null
    var Payable_After_Due_Date:String ?=null
    var issue_date1:String ?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)







        dialog = ProgressDialog(this)
        dialog!!.setTitle("Please Wait")
        dialog!!.setMessage("Loading...")
        dialog!!.show()
        connectivity=context.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager





        if (connectivity!=null){
            info=connectivity!!.activeNetworkInfo
            if(info !=null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
//                    binding.wifiLayout.visibility =View.VISIBLE
//                    binding.wifinotLayout.visibility=View.GONE
                    getbill()

                }

            }else{
                notconnect()
            }
        }

        click3.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        sharebill.setOnClickListener {
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
          // whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "-------Your Bill-------\t\nName :          ${name1!!.trim()}\nIssue Date       : " +
                    "${ issue_date1!!.trim()}\nDue Date         : ${ due_date1!!.trim()}\nBill Before Due Date : " +
                    "${Payable_Within_Due_Date!!.trim()}\nBill after Due date : ${ Payable_After_Due_Date!!.trim()}")

                startActivity(whatsappIntent)

        }
        take.setOnClickListener {

            val bitmap = getScreenShot(cons)   // layout to get screenshot
            if (bitmap != null) {
                saveToGallery(bitmap) }





        }




    }




    private fun getbill() {

        val number=intent.getStringExtra("number_ref")
        val api = "https://secure.ahmadsaeed.net/mepuucobill.api/mepcobill.php?no=${number.toString()}"


        val queue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(
            Request.Method.GET, api,
            { response ->
                dialog!!.dismiss()
                try {
                    val jsonObject = JSONObject(response)
                         issue_date1 = jsonObject.getString("issue_date")
                         due_date1 = jsonObject.getString("due_date")
                       name1 = jsonObject.getString("name")
                        Payable_Within_Due_Date = jsonObject.getString("Payable_Within_Due_Date")
                       Payable_After_Due_Date = jsonObject.getString("Payable_After_Due_Date")

                    cons.visibility =View.VISIBLE

                    name.text =name1.toString().trim()
                    issue_date.text =issue_date1.toString().trim()
                    due_date.text =due_date1.toString().trim()
                    before_pay.text =Payable_Within_Due_Date.toString().trim()
                    last_pay.text =Payable_After_Due_Date.toString().trim()

                    if (name1!!.isEmpty()||issue_date1!!.isEmpty()||Payable_After_Due_Date!!.isEmpty()||Payable_Within_Due_Date!!.isEmpty()||issue_date1==" "){

                        notfound()
                    }
//


                } catch (e: JSONException) {
                    //e.printStackTrace()
                    notfound()
                }
            }) { error ->
            // Toast.makeText(applicationContext, "" + error, Toast.LENGTH_SHORT).show()
            dialog!!.dismiss()
            notfound()

            // dialog!!.dismiss()
        }

        queue.add(stringRequest)

    }

    private fun notfound() {
        val dialgo = Dialog(this)
        dialgo.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialgo.setCancelable(false)
        dialgo.setContentView(R.layout.notfound)





        val window = dialgo.window
        window!!.setLayout(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialgo.btn_no.setOnClickListener {
            dialgo.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
        }
//        dialgo.btn_yes.setOnClickListener {
//
//            dialgo.dismiss()
//
//            finishAffinity()
//            System.exit(0)
//        }
        dialgo.show()

    }

    private fun notconnect() {

        dialog!!.dismiss()

        val dialgo1 = Dialog(this)
        dialgo1.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialgo1.setCancelable(false)
        dialgo1.setContentView(R.layout.connectivity)





        val window = dialgo1.window
        window!!.setLayout(
            ConstraintLayout.LayoutParams.WRAP_CONTENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        dialgo1.btn_no.setOnClickListener {
            dialgo1.dismiss()
            startActivity(Intent(this, MainActivity::class.java))
        }
//        dialgo.btn_yes.setOnClickListener {
//
//            dialgo.dismiss()
//
//            finishAffinity()
//            System.exit(0)
//        }
        dialgo1.show()

    }

    private fun getScreenShot(v: View): Bitmap? {
        var screenshot: Bitmap ?=null
        try {
            screenshot =Bitmap.createBitmap(v.measuredWidth, v.measuredHeight,Bitmap.Config.ARGB_8888)
            val canvas = Canvas(screenshot)
            v.draw(canvas)
        }catch (e: Exception){
            Log.e("GFG", "Failed to capture screenshot Because:" +e.message)
        }
        return screenshot
    }

    private fun saveToGallery(bitmap: Bitmap) {

        val filename ="${System.currentTimeMillis()}.jpg"
        var output_Stream : OutputStream? =null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            this.contentResolver?.also { resolver ->

                val contentValues = ContentValues().apply {

                    put(MediaStore.MediaColumns.DISPLAY_NAME,filename)
                    put(MediaStore.MediaColumns.MIME_TYPE,"image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri: Uri? =resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)

                output_Stream=imageUri?.let { resolver.openOutputStream(it) }
            }

        }else{

            val imageDir =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imageDir,filename)
            output_Stream =  FileOutputStream(image)

        }

        output_Stream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,it)
            Toast.makeText(this,"Downloaded",Toast.LENGTH_SHORT).show()
        }
    }
    override fun onBackPressed() {
        val dialgo= Dialog(this)
        dialgo.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialgo.setCancelable(false)
        dialgo.setContentView(R.layout.custom_dialog)
        val window = dialgo.window
        window!!.setLayout( ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        dialgo.btn_no.setOnClickListener {
            dialgo.dismiss()
        }
        dialgo.btn_yes.setOnClickListener {
            dialgo.dismiss()

            //System.exit(0)
            startActivity(Intent(this, MainActivity::class.java))
        }
        dialgo.show()

    }
}