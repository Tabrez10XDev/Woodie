package pro.lj.woodie02.ui.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import pro.lj.woodie02.R
import pro.lj.woodie02.databinding.FragmentLocationBinding


class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var pointAnnotation: PointAnnotation
    val POINT: Point = Point.fromLngLat( 80.046087,12.823530)
    var mapView: MapView? = null

    private val BOUNDS_ID = "BOUNDS_ID"
    private val SAN_FRANCISCO_BOUND: CameraBoundsOptions = CameraBoundsOptions.Builder()
        .bounds(
            CoordinateBounds(
                Point.fromLngLat(80.035411, 12.814117),
                Point.fromLngLat(80.054792, 12.837574),
                false
            )
        )
        .minZoom(12.0)
        .build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iconBitmap = convertDrawableToBitmap(AppCompatResources.getDrawable(requireContext(), R.drawable.red_marker))
        mapView = binding.mapView


        mapView?.getMapboxMap()?.loadStyle(
            style(Style.SATELLITE_STREETS) {
                +geoJsonSource(BOUNDS_ID) {
                    featureCollection(FeatureCollection.fromFeatures(listOf()))
                }
            }
        ) {
            prepareAnnotationMarker(mapView!!, iconBitmap!!)
            setupBounds(SAN_FRANCISCO_BOUND)
        }

    }

    private fun showBoundsArea(boundsOptions: CameraBoundsOptions) {
        val source = mapView?.getMapboxMap()?.getStyle()!!.getSource(BOUNDS_ID) as GeoJsonSource
        val bounds = boundsOptions.bounds
        val list = mutableListOf<List<Point>>()
        bounds?.let {
                val northEast = it.northeast
                val southWest = it.southwest
                val northWest = Point.fromLngLat(southWest.longitude(), northEast.latitude())
                val southEast = Point.fromLngLat(northEast.longitude(), southWest.latitude())
                list.add(
                    mutableListOf(northEast, southEast, southWest, northWest, northEast)
                )
            }

        source.geometry(
            Polygon.fromLngLats(
                list
            )
        )
    }

    private fun setupBounds(bounds: CameraBoundsOptions) {
        mapView?.getMapboxMap()?.setBounds(bounds)
        showBoundsArea(bounds)
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
        pointAnnotationManager?.addClickListener(OnPointAnnotationClickListener {
            Log.d("Taby",it.point.latitude().toString() + " -- " + it.point.longitude() )
            true
        })
        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
    }


}