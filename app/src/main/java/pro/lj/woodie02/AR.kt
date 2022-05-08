package pro.lj.woodie02

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import pro.lj.woodie02.databinding.ArScreenBinding

class AR : AppCompatActivity() {
    private lateinit var binding: ArScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ArScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Log.d("TAGGG","here")

//        if (!checkIsSupportedDeviceOrFinish(this)) {
//            return;
//        }
//        val arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
//
//        arFragment?.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
//            val anchor = hitResult.createAnchor()
//            placeObject(arFragment, anchor, 0)
//        }

    }

//    private fun placeObject(arFragment: ArFragment, anchor: Anchor, id: Int) {
//        ModelRenderable.builder()
//            .setSource(
//                arFragment.context,
//                RenderableSource.builder()
//                    .setSource(arFragment.context,
//                        Uri.parse("https://firebasestorage.googleapis.com/v0/b/woodie-13061.appspot.com/o/Persian.glb?alt=media&token=7df11ae2-60d9-4722-8f41-8cb95ed7cb0e"),
//                        RenderableSource.SourceType.GLB)
//                    .build()
//
//               // Uri.parse("https://firebasestorage.googleapis.com/v0/b/woodie-13061.appspot.com/o/bear%20(1).sfb?alt=media&token=2986ba67-cc24-409b-95b4-de60a8eb7dfb"),
//
//                )
//            .build()
//            .thenAccept { modelRenderable: ModelRenderable ->
//                Log.d("TAGGG","plced vro")
//                addNodeToScene(
//                    arFragment,
//                    anchor,
//                    modelRenderable
//                )
//            }
//            .exceptionally { throwable: Throwable ->
//                Log.d("mine",throwable.message.toString())
//                Toast.makeText(arFragment.context, "Error:" + throwable.message, Toast.LENGTH_LONG)
//                    .show()
//                null
//            }
//    }

    private fun addNodeToScene(arFragment: ArFragment, anchor: Anchor, renderable: Renderable) {

        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(arFragment.transformationSystem)
        node.renderable = renderable
        node.setParent(anchorNode)
        arFragment.arSceneView.scene.addChild(anchorNode)
        node.select()
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
}