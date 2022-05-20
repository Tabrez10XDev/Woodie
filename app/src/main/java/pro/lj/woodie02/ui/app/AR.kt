package pro.lj.woodie02.ui.app

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import pro.lj.woodie02.R
import pro.lj.woodie02.data.Tree
import pro.lj.woodie02.databinding.ArScreenBinding
import java.util.*

class AR : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ArScreenBinding
    lateinit var tts: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArScreenBinding.inflate(layoutInflater)
        val view = binding.root
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(view)
        Log.d("TAGGG","here")
        val intent = this.intent
        val bundle = intent.getBundleExtra("item")
        val tree : Tree = bundle?.getSerializable("item") as Tree
        tts = TextToSpeech(this, this)


        binding.apply {
            tvName.text = tree.name
            tvTree.text = spannableText("Height -" + tree.height,0,8)
            tvHeart.text = spannableText("Lifespan - " + tree.lifespan + " Years", 0, 10)
            tvFace.text = spannableText("Uses - " + tree.uses, 0, 6)
            tvPlant.text = spannableText("Scientific Names - " + tree.scientificName,0 , 18)
            tvHand.text = spannableText("Vernacular Names - " + tree.vernacularNames, 0, 18)
        }

        binding.btnPlay.setOnClickListener {
            tts.speak(tree.uses, TextToSpeech.QUEUE_FLUSH, null,"")
        }
//        if (!checkIsSupportedDeviceOrFinish(this)) {
//            return;
//        }
//        val arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
//
//        arFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
//            val anchor = hitResult.createAnchor()
//            placeObject(arFragment, anchor, tree.modelUri)
//        }

    }

    private fun placeObject(arFragment: ArFragment, anchor: Anchor, id: String) {
        ModelRenderable.builder()
            .setSource(
                arFragment.context,
                Uri.parse(id),
            )
            .build()
            .thenAccept { modelRenderable: ModelRenderable ->
                Log.d("TAGGG","plced vro")
                addNodeToScene(
                    arFragment,
                    anchor,
                    modelRenderable
                )
            }
            .exceptionally { throwable: Throwable ->
                Log.d("TAGGG",throwable.message.toString())
                Toast.makeText(arFragment.context, "Error:" + throwable.message, Toast.LENGTH_LONG)
                    .show()
                null
            }
    }

    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {

        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)

        node.select()
    }

    private fun spannableText(text: String, start: Int, end: Int) : SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder(text)
        spannableStringBuilder.setSpan(
                ForegroundColorSpan(getColor(R.color.textGreen)),
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
        Log.d("TAGGG", "Open: $openGlVersionString")

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
                Log.e("TTS","Unknown error occured")
            } else {
                binding.btnPlay.isEnabled = true
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }    }
}