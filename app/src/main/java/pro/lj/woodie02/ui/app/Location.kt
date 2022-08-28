package pro.lj.woodie02.ui.app
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.Annotation
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import pro.lj.woodie02.R
import pro.lj.woodie02.databinding.FragmentLocationBinding


class Location : AppCompatActivity() {
    val SELECTED_ADD_COEF_PX = 25
    val POINT: Point = Point.fromLngLat(0.381457, 6.687337)

    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var pointAnnotation: PointAnnotation

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val iconBitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(this, R.drawable.red_marker))


        binding.mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            prepareAnnotationMarker(binding.mapView, iconBitmap!!)
          //  prepareViewAnnotation(binding.mapView)
        }
    }




    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }


    private fun prepareAnnotationMarker(mapView: MapView, iconBitmap: Bitmap) {
        val annotationPlugin = mapView.annotations
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(POINT)
            .withIconImage(iconBitmap)
            .withIconAnchor(IconAnchor.BOTTOM)
            .withDraggable(false)
        pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
    }

}





