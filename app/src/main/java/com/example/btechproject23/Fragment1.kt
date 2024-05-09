package com.example.btechproject23

import android.graphics.Color
import android.graphics.Paint
import android.icu.util.UniversalTimeScale.toLong
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidplot.util.Redrawer
import com.androidplot.xy.AdvancedLineAndPointRenderer
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.StepMode
import com.androidplot.xy.XYPlot
import com.androidplot.xy.XYSeries
import java.lang.ref.WeakReference

private const val xRange: Int = 128*4
private const val yRange: Int = 1250

class Fragment1 : Fragment() {
    private var plot: XYPlot? = null
    private var plot1: XYPlot? = null
    private var plot2: XYPlot? = null
    private var redrawer: Redrawer? = null
    private var timeCompleteRequired: Double = 4.0 // In Seconds
    private val updateFreq: Double = xRange / timeCompleteRequired

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view :View = inflater.inflate(R.layout.fragment_1, container, false)

//    References to the XYPlot views in the app layout
        plot = view.findViewById(R.id.plot)
        plot1 = view.findViewById(R.id.plot1)
        plot2 = view.findViewById(R.id.plot2)

//    Custom Formatter used to control the appearance of the plotted data on the Graph
        val formatter = MyFadeFormatter(xRange, Color.GREEN)
        formatter.isLegendIconEnabled = false   // I have used this to disable the Legends

//    Data Manipulation of Different Plots
        val ecgData: Array<Int> = arrayOf(
            10,-5,-7,1,-3,4,-2,5,-2,7, -1, 0, -3, 0, -1, 12, 21, 62, 93, 115, 125, 137, 166, 177,
            187, 182, 183, 183, 172, 150, 138, 120, 113, 70, 32, 16, 6, 3, -4, -2, -3, 4, 7, -2, 2, -1, 0,
            0, 5, 2, 5, -10, -63, -90, -133, -74, 103, 295, 492, 685, 876, 1049, 1049, 886, 705, 510, 309,
            129, -43, -110, -87, -71, -39, -7, 1, -4, 4, 1, -3, -4, 4, 0, 4, -1, 10, 24, 42, 94, 122, 170, 210,
            235, 252, 285, 308, 341, 363, 387, 393, 416, 422, 437, 442, 459, 466, 470, 465, 469, 461, 442, 430,
            409, 401, 384, 354, 332, 311, 285, 244, 213, 190, 152, 120, 92, 29, 5, -5
        )
        val ecgData1: Array<Int> = ecgData.copyOf()
        for (i in ecgData1.indices) {ecgData1[i] = ecgData1[i] / 2}
        val ecgData2: Array<Int> = ecgData.copyOf()
        for (i in ecgData2.indices) {ecgData2[i] = -1*ecgData2[i]}

//    This is called to setup and Configure the Plot with its respective ECG Data
        configureAndSetupPlot(plot, ecgData, formatter)
        configureAndSetupPlot(plot1, ecgData1, formatter)
        configureAndSetupPlot(plot2, ecgData2, formatter)

//    To set boundaries, x-y axis, grid lines for the plot
        configurePlot(plot)
        configurePlot(plot1)
        configurePlot(plot2)

        return view
    }
    // This Section controls the Fading effect of the ECG line on the plot,
    class MyFadeFormatter(private val trailSize: Int, private val lineColor: Int) : AdvancedLineAndPointRenderer.Formatter() {
        override fun getLinePaint(thisIndex: Int, latestIndex: Int, seriesSize: Int): Paint {

//         thisIndex - The position at which last Datapoint is being added or updated within the series.
//         latestIndex -  the index of the most recent data point in a series.
//         Offset - Determine the relative position of the current data point in the series compared to the latest data point
            val offset: Int = if (thisIndex > latestIndex) {
                latestIndex + (seriesSize - thisIndex)
            } else {
                latestIndex - thisIndex
            }

            val scale = 255f / (trailSize * 2.5)  //  Scaling Factor
//            Its used to control the transparency of the plotted line on the graph
//            Higher value of alpha, means lesser will be the transparency
            val alpha = (255 - offset * scale).toInt()

            val linePaint = Paint()
            linePaint.color = lineColor
            linePaint.alpha = if (alpha > 0) alpha else 0       // setting color while fading, alpha = 0 means no line is visible.
            linePaint.strokeWidth = 4f

            return linePaint
        }
    }

    // This function configures and sets up a given XYPlot with ECG data.
    private fun configureAndSetupPlot(plot: XYPlot?, data: Array<Int>, formatter: MyFadeFormatter) {
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
        redrawer = Redrawer(plot, 50f, true)
    }

    //     It sets up the plot's boundaries, step modes, and the number of lines per range label
    private fun configurePlot(plot: XYPlot?) {
        plot?.setRangeBoundaries(-yRange, yRange, BoundaryMode.FIXED)   // To set the boundaries range along y - axis
        plot?.setDomainBoundaries(0, xRange, BoundaryMode.FIXED)    // To set the boundaries range along x - axis
        plot?.graph?.linesPerDomainLabel = 500
        plot?.graph?.linesPerRangeLabel = 500
        plot?.setDomainStep(StepMode.INCREMENT_BY_PIXELS, 25.0)
        plot?.setRangeStep(StepMode.INCREMENT_BY_PIXELS, 25.0)
    }

    //     It's responsible for providing data points and controlling the real-time update of data on the plot, It set ups a background thread to continuously date the data points
    class ECGModel(private val inputData: Array<Int>, private val updateFreqHz: Double) : XYSeries {
        private val data = arrayOfNulls<Int?>(xRange)       // NULL array of length xRange which will be used to provide the Y-ais value of the plot
        private var delayMs: Double = 0.0       // To set the delay between updates in milliseconds
        private var thread: Thread? = null  // To run the background thread to update the data.
        private var keepRunning = false     // controls whether the background thread continues to run, will set to true when the thread is started
        private var latestIndex : Int = 0   // to keeps track of the index where the latest data point should be updated
        private var dataSize = inputData.size   // size of the input array to be plotted

        //    reference is used to communicate with the renderer associated with the plot.
//    It allows you to update the latest index in the renderer for real-time plotting
        private var rendererRef: WeakReference<AdvancedLineAndPointRenderer>? = null

        init {

            //      It sets the initial value of each element to 0. This essentially sets the data array to show a horizontal line at the y-coordinate 0.
            //      This horizontal line represents the starting state of the ECG signal.
            for (i in data.indices) {
                data[i] = 0
            }

            //      the time (in milliseconds) between each update of the ECG data
            delayMs = 1000 / updateFreqHz

            //      To continuously update the ECG data
            thread = Thread {
                try {
                    //      To update the ECG data in real-time.
                    while (keepRunning) {
                        if (latestIndex >= xRange) {
                            latestIndex = 0
                        }

                        data[latestIndex] = inputData[latestIndex%dataSize]

                        //     This creates a gap between the old and new data points, giving the appearance of a line moving across the plot.
                        if (latestIndex < data.size - 1) {
                            data[latestIndex + 1] = null
                        }

                        if (rendererRef?.get() != null) {
                            rendererRef?.get()?.setLatestIndex(latestIndex)
                            Thread.sleep(delayMs.toLong())
                        } else {
                            keepRunning = false
                        }
                        latestIndex++
                    }
                } catch (e: InterruptedException) {
                    keepRunning = false
                }
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

        //    Returns the size of the data array
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

    override fun onDestroyView() {
        super.onDestroyView()
        redrawer?.finish()
    }

}