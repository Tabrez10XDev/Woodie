package pro.lj.woodie02.ui.app

import android.R.array
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.net.Uri
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.SceneView
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.CameraStream
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pro.lj.woodie02.R
import pro.lj.woodie02.adapters.SliderAdapter
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.ArScreenBinding
import java.lang.Exception
import java.lang.Math.abs
import java.util.*


class AR : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ArScreenBinding
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var arFragment: ArFragment
    private val arSceneView get() = arFragment.arSceneView
    private val scene get() = arSceneView.scene
    private var model: Renderable? = null
    lateinit var tts: TextToSpeech
    private lateinit var modelUri: String
    var status = 1
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArScreenBinding.inflate(layoutInflater)
        val view = binding.root
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(view)
        val intent = this.intent
        val bundle = intent.getBundleExtra("item")
        val tree: Tree = bundle?.getSerializable("item") as Tree
        modelUri = tree.modelUri
        tts = TextToSpeech(this, this)
        tts.setSpeechRate(1.2f)
        setUpViewPager(tree)
        binding.btnClose.setOnClickListener {
            onBackPressed()
        }
        binding.apply {
            tvName.text = " " + tree.name
            tvCommonName.text = spannableText("Common Name - " + tree.commonName, 0, 13)
            tvBotanicalDescription.text = spannableText("Botanical Description - " + tree.botanicalDes, 0, 23)
            tvFamily.text = spannableText("Family - " + tree.family, 0, 8)
            tvUses.text = spannableText("Uses - " + tree.uses, 0, 6)
            tvScientificName.text = spannableText("Scientific Name - " + tree.scientificName, 0, 17)
            tvVernacularNames.text = spannableText("Tamil Name - " + tree.tamilName,0,12)
            tvFlowers.text = spannableText("Flowers - " + tree.flowers,0,9)
            tvHabit.text = spannableText("Habit - " + tree.habit,0,7)
            tvLeaves.text = spannableText("Leaves - " + tree.leaves,0,8)
            tvFruits.text = spannableText("Fruits - " + tree.fruits,0,8)
            tvOrigin.text = spannableText("Origin - " + tree.origin,0,8)
        }

        binding.btnPlay.setOnClickListener {
            if(!tts.isSpeaking){
                tts.speak(tree.uses, TextToSpeech.QUEUE_FLUSH, null, "")
                binding.btnPlay.setImageResource(R.drawable.ic_stop_final)

            }
            else{
                tts.stop()
                binding.btnPlay.setImageResource(R.drawable.ic_play_final)
            }
        }
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
            arFragment = (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment).apply {
                setOnSessionConfigurationListener { session, config ->
                    instructionsController = null
                }

                setOnTapArPlaneListener(::onTapPlane)
            }

            CoroutineScope(Dispatchers.Main).launch {
                loadModels(tree)
            }







    }

    private fun loadModels(tree: Tree) {
        try{
            Log.d("Taby",tree.modelUri)

            ModelRenderable.builder()
                .setSource(applicationContext, Uri.parse(tree.modelUri))
                .setIsFilamentGltf(true)
                .build()
                .thenAccept {
                    model = it
                }
        }
        catch (e:Exception){
            //Toast.makeText(this,e.localizedMessage.toString(),Toast.LENGTH_SHORT).show()
        }

    }

    private fun onTapPlane(hitResult: HitResult, plane: Plane, motionEvent: MotionEvent) {
        if(modelUri == "") {
            Toast.makeText(applicationContext, "AR Coming soon...", Toast.LENGTH_SHORT).show()
            return
        }
        if (model == null) {
            Toast.makeText(applicationContext, "Loading...", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the Anchor.
        scene.addChild(AnchorNode(hitResult.createAnchor()).apply {
            // Create the transformable model and add it to the anchor.
            addChild(TransformableNode(arFragment.transformationSystem).apply {
                renderable = model
                renderableInstance?.animate(true)?.start()
            })
        })
    }

    private fun changeTerraform(tree: Tree){
        if(status == 1){
            binding.tvName.updateLayoutParams<ConstraintLayout.LayoutParams> {
                horizontalBias = 1f
                marginEnd = -16
            }
            binding.tvName.text = ""
            binding.viewPager.visibility = View.INVISIBLE
            val params: ViewGroup.LayoutParams = binding.layio.layoutParams
            params.height = 0
            binding.layio.layoutParams = params
            status = 0
        }

        else{
            binding.tvName.updateLayoutParams<ConstraintLayout.LayoutParams> {
                horizontalBias = 0.5f
                marginEnd = 0

            }
            binding.tvName.text = tree.name
            binding.viewPager.visibility = View.VISIBLE
            val params: ViewGroup.LayoutParams = binding.layio.layoutParams
            params.height = 300.px
            binding.layio.layoutParams = params
            status = 1
        }
    }


    private fun setUpViewPager(tree: Tree){
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r: Float = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f

        })
        val trees = listOf<Tree>(tree)
        sliderAdapter = SliderAdapter()
        binding.viewPager.apply {
            adapter = sliderAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            setPageTransformer(compositePageTransformer)
            visibility = View.VISIBLE
        }
        sliderAdapter.differ.submitList(trees)

    }




    private fun spannableText(text: String, start: Int, end: Int) : SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(text)
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(getColor(R.color.textGreen)),
            start,
            end,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )

        return spannableStringBuilder
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e("TAG", "Sceneform requires Android N or later")
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        val openGlVersionString =
            (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion

        return true
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onInit(p0: Int) {
        if (p0 == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Unknown error occured")
            } else {
                binding.btnPlay.visibility = View.VISIBLE
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String) {
                        Log.i("TextToSpeech", "On Start")
                    }

                    override fun onDone(utteranceId: String) {
                        binding.btnPlay.setImageResource(R.drawable.ic_btn_play)
                    }

                    override fun onError(utteranceId: String) {
                        Log.i("TextToSpeech", "On Error")
                    }
                })
            }



        } else {
            Log.e("TTS", "Initilization Failed!")
        }
    }

    val Int.px get() = (this * Resources.getSystem().displayMetrics.density).toInt()

}