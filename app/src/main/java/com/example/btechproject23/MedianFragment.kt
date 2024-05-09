package com.example.btechproject23

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidplot.util.Redrawer
import com.androidplot.xy.AdvancedLineAndPointRenderer
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.StepMode
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import java.lang.ref.WeakReference

private var xRange: Int = 0
private const val yRange: Int = 1500

class MedianFragment : Fragment() {
    private var plot: XYPlot? = null
    private var redrawer: Redrawer? = null
    private val updateFreq: Int = 700

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view :View = inflater.inflate(R.layout.fragment_median, container, false)

        plot = view.findViewById(R.id.plot)

        val formatter = MyFadeFormatter(xRange, Color.GREEN)
        formatter.isLegendIconEnabled = false

        val ecgData: Array<Double> = arrayOf(
            90.19999999999999, 60.2, 39.400000000000006, 25.0, 15.599999999999994, 13.400000000000006, 12.799999999999997, 11.200000000000003,
            16.0, 14.799999999999997, 16.799999999999997, 12.599999999999994, 10.799999999999997, 15.400000000000006, 15.0, 15.799999999999997,
            13.400000000000006, 14.0, 13.599999999999994, 13.599999999999994, 14.200000000000003,  13.200000000000003, 20.200000000000003,
            27.400000000000006, 46.599999999999994, 67.0, 94.4, 122.4, 145.0, 167.6, 185.8, 204.2, 217.60000000000002, 228.2, 236.8, 235.0,
            226.8, 216.8,  199.60000000000002, 183.0, 162.8, 142.2, 115.19999999999999, 85.0, 57.2, 36.0, 20.400000000000006, 13.599999999999994,
            12.599999999999994, 10.400000000000006, 13.799999999999997, 15.200000000000003, 14.0, 16.799999999999997, 15.400000000000006,
            10.799999999999997, 11.400000000000006, 12.599999999999994, 14.200000000000003, 15.0, 12.400000000000006, 11.599999999999994,
            6.200000000000003, -13.0, -35.0, -68.8, -81.8, -52.0, 38.8, 183.4, 380.4, 603.4, 832.6, 1017.0, 1115.0, 1119.4, 1029.2, 844.6,
            623.8, 400.2, 200.60000000000002, 55.8, -40.2, -74.6, -65.2, -38.6, -14.399999999999999, -0.20000000000000284, 14.799999999999997,
            13.799999999999997, 11.799999999999997, 10.200000000000003, 13.200000000000003, 13.599999999999994, 13.400000000000006,
            13.799999999999997, 14.400000000000006, 12.400000000000006, 11.0, 16.200000000000003, 21.599999999999994, 29.799999999999997, 42.0,
            66.4,  95.80000000000001, 125.4, 156.6, 191.60000000000002, 224.2, 256.0, 288.2, 322.6, 351.4, 383.2, 410.8, 434.2,
            453.79999999999995, 473.6, 491.20000000000005, 509.0, 522.8, 536.8, 548.6, 559.0,  567.4, 574.8, 577.2, 568.2, 560.0, 543.8,
            530.0, 508.79999999999995, 488.20000000000005, 466.0, 444.4, 412.8, 383.8, 349.6, 317.0, 278.0, 240.60000000000002, 193.75, 150.0,
            110.25, 74.0,  48.5, 32.5, 21.25
        )
        xRange = ecgData.size
        configureAndSetupPlot(plot, ecgData, formatter)
        configurePlot(plot)

        return view
    }

    // This Section controls the Fading effect of the ECG line on the plot,
    class MyFadeFormatter(private val trailSize: Int, private val lineColor: Int)
        : AdvancedLineAndPointRenderer.Formatter() {
        override fun getLinePaint(thisIndex: Int, latestIndex: Int, seriesSize: Int): Paint {
            val linePaint = Paint()
            linePaint.color = lineColor
            return linePaint
        }
    }

    // This function configures and sets up a given XYPlot with ECG data.
    private fun configureAndSetupPlot(plot: XYPlot?, data: Array<Double>, formatter: MyFadeFormatter) {
        val ecgSeries = ECGModel(data, updateFreq)
        activity?.runOnUiThread {
            plot?.addSeries(ecgSeries, formatter)
        }

//    To update the plot in real time
//    Weak Reference is a class of java for managing object references, here its used to maintain a reference to the renderer associated with the plot
//    Here we have used Weak Reference because doesn't prevent an object from being garbage collected when no strong references exist, it helps
//    manage memory and avoids potential memory leaks in scenarios where the renderer or its associated objects are no longer needed
        ecgSeries.start(WeakReference(plot?.getRenderer(AdvancedLineAndPointRenderer::class.java)))

//    Used to redraw the plot at a specific refresh rate, maxRefreshRate is in millisecond
        redrawer = Redrawer(plot, 10f, true)
    }

//     It sets up the plot's boundaries, step modes, and the number of lines per range label
    private fun configurePlot(plot: XYPlot?) {
        plot?.setRangeBoundaries(-yRange, yRange, BoundaryMode.FIXED)   // To set the boundaries range along y - axis
        plot?.setDomainBoundaries(0, xRange, BoundaryMode.FIXED)    // To set the boundaries range along x - axis
        plot?.graph?.linesPerDomainLabel = 500
        plot?.graph?.linesPerRangeLabel = 500
        plot?.setDomainStep(StepMode.INCREMENT_BY_PIXELS, 5.0)
        plot?.setRangeStep(StepMode.INCREMENT_BY_PIXELS, 5.0)
    }

//     It's responsible for providing data points and controlling the real-time update of data on the plot, It set ups a background thread to continuously date the data points
    class ECGModel(private val inputData: Array<Double> , private val updateFreqHz: Int) : XYSeries {
        private val data = arrayOfNulls<Double?>(xRange)       // NULL array of length xRange which will be used to provide the Y-ais value of the plot
        private var delayMs: Long = 0       // To set the delay between updates in milliseconds
        private var thread: Thread? = null  // To run the background thread to update the data.
        private var keepRunning = false     // controls whether the background thread continues to run, will set to true when the thread is started
        private var latestIndex : Int = 0   // to keeps track of the index where the latest data point should be updated
        private var dataSize = inputData.size   // size of the input array to be plotted

//    reference is used to communicate with the renderer associated with the plot.
//    It allows you to update the latest index in the renderer for real-time plotting
        private var rendererRef: WeakReference<AdvancedLineAndPointRenderer>? = null

        init {
            for (i in data.indices) {
                data[i] = inputData[i]
            }
        }

//    To start the background thread responsible for data updates.
//    It takes a WeakReference to the renderer as an argument and sets the rendererRef to this reference.
//    It also sets keepRunning to true and starts the thread.
        fun start(rendererRef: WeakReference<AdvancedLineAndPointRenderer>) {
            this.rendererRef = rendererRef
            keepRunning = true
            thread?.start()
        }

//            Returns the size of the data array
        override fun size(): Int {
            return data.size
        }

        //    returns the X-value (index) for a given data point.
        override fun getX(index: Int): Number {
            return index
        }

        //    returns the Y-value (ECG data point) for a given index
        override fun getY(index: Int): Number? {
            return data[index]
        }

        //    returns a title or label for the series, which is used in the plot
        override fun getTitle(): String {
            return "ECG Signal"
        }
    }

}