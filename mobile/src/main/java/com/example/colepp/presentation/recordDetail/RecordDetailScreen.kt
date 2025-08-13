package com.example.colepp.presentation.recordDetail

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.colepp.R
import com.example.colepp.common.composeUI.DefaultButton
import com.example.colepp.common.composeUI.LoadingScreen
import com.example.colepp.common.composeUI.Toolbar
import com.example.colepp.common.composeUI.rememberMarker
import com.example.colepp.common.utils.SavingDatasetStatus
import com.example.colepp.common.utils.ScreenState
import com.example.colepp.common.utils.Size
import com.example.colepp.common.utils.TextStyles
import com.example.colepp.common.utils.bottomAxisValueFormatter
import com.example.colepp.common.utils.columnColors
import com.example.colepp.common.utils.legendItemLabelComponent
import com.example.colepp.common.utils.markerValueFormatter
import com.example.colepp.common.utils.polarLineColorId
import com.example.colepp.common.utils.smartwatchLineColorId
import com.example.colepp.common.utils.startAxisValueFormatter
import com.example.colepp.common.utils.toCronometerFormat
import com.example.colepp.common.utils.toFormat
import com.example.colepp.common.utils.yDecimalFormat
import com.example.colepp.database.model.HeartRateGenericData
import com.example.colepp.database.model.RecordEntity
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.Calendar
import java.util.TimeZone

private val LegendLabelKey = ExtraStore.Key<Set<String>>()

@Composable
fun RecordDetailScreen(
    viewModel: RecordDetailViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    createDatasetFile: (Long, (File) -> Unit) -> Unit,
    shareFile: (Uri) -> Unit
) {
    val screenState = viewModel.screenState.collectAsState()
    val record = viewModel.record.collectAsState()
    val datasetGeneratorDownloadStatus = viewModel.saveDatasetDownloadStatus.collectAsState()
    val datasetGeneratorShareStatus = viewModel.saveDatasetShareStatus.collectAsState()
    val heartRateSmartWatch = viewModel.heartRateSmartWatch.collectAsState()
    val heartRatePolar = viewModel.heartRatePolar.collectAsState()

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(heartRateSmartWatch.value, heartRatePolar.value) {
        modelProducer.runTransaction {
            val keys = mutableSetOf<String>()
            if (heartRatePolar.value.isNotEmpty()) {
                val initialTime = heartRatePolar.value.first().timestamp
                lineSeries {
                    series(
                        x = heartRatePolar.value.map { (it.timestamp - initialTime) / 1_000_000_000L },
                        y = heartRatePolar.value.map { it.heartRate }
                    )
                }
                keys.add("Polar")
            }
            if (heartRateSmartWatch.value.isNotEmpty()) {
                val skewTime = record.value?.clockSkewSmartwatchNanos ?: 0L
                val initialTime = heartRateSmartWatch.value.first().timestamp + skewTime
                lineSeries {
                    series(
                        x = heartRateSmartWatch.value.map { (it.timestamp - initialTime + skewTime) / 1_000_000_000L },
                        y = heartRateSmartWatch.value.map { it.heartRate }
                    )
                }
                keys.add("Smartwatch")
            }
            extras { it[LegendLabelKey] = keys }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getContent()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.White,
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Toolbar(
                title = stringResource(R.string.record_detail),
                onBackPressed = onBackPressed,
                hasCloseIcon = false
            )
            when (screenState.value) {
                ScreenState.Loading -> {
                    LoadingScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                ScreenState.Content -> {
                    record.value?.let {
                        ScreenContent(
                            record = it,
                            downloadDataset = {
                                createDatasetFile(
                                    it.id,
                                    viewModel::onFileCreatedDownload
                                )
                            },
                            shareDataset = {
                                createDatasetFile(
                                    it.id,
                                    viewModel::onFileCreatedShare
                                )
                            },
                            modelProducer = modelProducer,
                            heartRateSmartWatch = heartRateSmartWatch.value,
                            heartRatePolar = heartRatePolar.value
                        )
                    }
                }

                is ScreenState.Error -> {
                    // Show error state
                }
            }
        }
    }
    datasetGeneratorDownloadStatus.value?.let { status ->
        Dialog(
            onDismissRequest = { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(Size.size05)
                    )
                    .padding(Size.size05),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Size.size04)
            ) {
                when (status) {
                    SavingDatasetStatus.Saving -> {

                        Text(
                            text = stringResource(R.string.saving_dataset),
                            modifier = Modifier.padding(Size.size05),
                            style = TextStyles.TitleM
                        )
                        LoadingScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Size.size08)
                        )
                    }

                    is SavingDatasetStatus.Error -> {
                        Image(
                            modifier = Modifier
                                .padding(bottom = Size.size03),
                            painter = painterResource(id = R.drawable.ic_alert),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.error_saving_dataset),
                            style = TextStyles.TitleM
                        )
                        Text(
                            text = stringResource(
                                R.string.error_saving_dataset_message,
                                status.message
                            ),
                            style = TextStyles.TextS,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        DefaultButton(
                            text = stringResource(R.string.close),
                            onClick = {
                                viewModel.dismissSaveDatasetDownloadStatus()
                            }
                        )
                    }

                    is SavingDatasetStatus.Success -> {
                        Image(
                            modifier = Modifier
                                .padding(bottom = Size.size03),
                            painter = painterResource(id = R.drawable.ic_success),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.dataset_saved_successfully),
                            style = TextStyles.TitleM,
                        )
                        Text(
                            text = stringResource(
                                R.string.dataset_saved_successfully_message,
                                status.file.path.orEmpty()
                            ),
                            style = TextStyles.TextS,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        DefaultButton(
                            text = stringResource(R.string.close),
                            onClick = {
                                viewModel.dismissSaveDatasetDownloadStatus()
                            }
                        )
                    }
                }
            }
        }
    }
    datasetGeneratorShareStatus.value?.let { status ->
        Dialog(
            onDismissRequest = { }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(Size.size05)
                    )
                    .padding(Size.size05),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Size.size04)
            ) {
                when (status) {
                    SavingDatasetStatus.Saving -> {

                        Text(
                            text = stringResource(R.string.saving_dataset),
                            modifier = Modifier.padding(Size.size05),
                            style = TextStyles.TitleM
                        )
                        LoadingScreen(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = Size.size08)
                        )
                    }

                    is SavingDatasetStatus.Error -> {
                        Image(
                            modifier = Modifier
                                .padding(bottom = Size.size03),
                            painter = painterResource(id = R.drawable.ic_alert),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.error_saving_dataset),
                            style = TextStyles.TitleM
                        )
                        Text(
                            text = stringResource(
                                R.string.error_saving_dataset_message,
                                status.message
                            ),
                            style = TextStyles.TextS,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Start
                        )
                        DefaultButton(
                            text = stringResource(R.string.close),
                            onClick = {
                                viewModel.dismissSaveDatasetDownloadStatus()
                            }
                        )
                    }

                    is SavingDatasetStatus.Success -> {
                        shareFile(status.file)
                        viewModel.dismissSaveDatasetShareStatus()
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    record: RecordEntity,
    downloadDataset: () -> Unit,
    shareDataset: () -> Unit,
    modelProducer: CartesianChartModelProducer,
    heartRatePolar: List<HeartRateGenericData>,
    heartRateSmartWatch: List<HeartRateGenericData>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Size.size05)
            .padding(top = Size.size04),
        verticalArrangement = Arrangement.spacedBy(Size.size04)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Size.size04, Alignment.End)
        ) {
            Text(
                text = record.title,
                modifier = Modifier.weight(1f),
                style = TextStyles.TitleM,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_download),
                contentDescription = null,
                modifier = Modifier
                    .size(Size.size08)
                    .clickable { downloadDataset() }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_share),
                contentDescription = null,
                modifier = Modifier
                    .size(Size.size08)
                    .clickable { shareDataset() }
            )
        }
        if (record.description.isNotBlank()) {
            Text(
                text = stringResource(R.string.record_detail_description),
                modifier = Modifier.fillMaxWidth(),
                style = TextStyles.TextLSemiBold,
            )
            Text(
                text = record.description,
                modifier = Modifier.fillMaxWidth(),
                style = TextStyles.TextS,
            )
        }
        Text(
            text = stringResource(R.string.record_detail_initial_date),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyles.TextLSemiBold,
        )
        Text(
            text = Calendar.getInstance().apply {
                timeInMillis = record.starRecordingMilli
                timeZone = TimeZone.getDefault()
            }.time.toFormat(pattern = "dd/MM/yyyy HH:mm:ss"),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyles.TextS,
        )
        Text(
            text = stringResource(R.string.record_detail_time_duration),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyles.TextLSemiBold,
        )
        Text(
            text = (record.stopRecordingMilli - record.starRecordingMilli).toCronometerFormat(),
            modifier = Modifier.fillMaxWidth(),
            style = TextStyles.TextS,
        )
        if (heartRatePolar.isNotEmpty() && heartRateSmartWatch.isNotEmpty()) {
            HearthRateChart(modelProducer = modelProducer)
        } else if (heartRatePolar.isNotEmpty()) {
            HearthRateOnlyPolarChart(modelProducer = modelProducer)
        } else if (heartRateSmartWatch.isNotEmpty()) {
            HearthRateOnlySmartwatchChart(modelProducer = modelProducer)
        }
    }
}


@Composable
private fun HearthRateChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val columnColorsMapped = columnColors.map { colorResource(id = it) }
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(colorResource(id = polarLineColorId))),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(
                                                colorResource(id = polarLineColorId).copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ),
                        )
                    ),
            ),
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(colorResource(id = smartwatchLineColorId))),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(
                                                colorResource(id = smartwatchLineColorId).copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ),
                        )
                    ),
            ),
            legend =
                rememberHorizontalLegend(
                    items = { extraStore ->
                        extraStore[LegendLabelKey].forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(
                                        fill(columnColorsMapped[index]),
                                        CorneredShape.Pill
                                    ),
                                    legendItemLabelComponent,
                                    label,
                                )
                            )
                        }
                    },
                    padding = insets(top = 16.dp),
                ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = startAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            marker = rememberMarker(markerValueFormatter),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
private fun HearthRateOnlyPolarChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val markerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(yDecimalFormat)
    val polarLineColor = colorResource(id = R.color.heart_rate_polar_color)
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(polarLineColor)),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(
                                                polarLineColor.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ),
                        )
                    ),
            ),
            legend =
                rememberHorizontalLegend(
                    items = { extraStore ->
                        extraStore[LegendLabelKey].forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(fill(polarLineColor), CorneredShape.Pill),
                                    legendItemLabelComponent,
                                    label,
                                )
                            )
                        }
                    },
                    padding = insets(top = 16.dp),
                ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = startAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            marker = rememberMarker(markerValueFormatter),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
private fun HearthRateOnlySmartwatchChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    val markerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(yDecimalFormat)
    val smartwatchLineColor = colorResource(id = R.color.heart_rate_smartwatch_color)
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(smartwatchLineColor)),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(
                                                smartwatchLineColor.copy(alpha = 0.4f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                ),
                        )
                    ),
            ),
            legend =
                rememberHorizontalLegend(
                    items = { extraStore ->
                        extraStore[LegendLabelKey].forEachIndexed { index, label ->
                            add(
                                LegendItem(
                                    shapeComponent(fill(smartwatchLineColor), CorneredShape.Pill),
                                    legendItemLabelComponent,
                                    label,
                                )
                            )
                        }
                    },
                    padding = insets(top = 16.dp),
                ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = startAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisValueFormatter,
                label = legendItemLabelComponent
            ),
            marker = rememberMarker(markerValueFormatter),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Preview(showBackground = true)
@Composable
private fun ScreenContentPreview() {
    val modelProducer = remember { CartesianChartModelProducer() }
    // Use `runBlocking` only for previews, which don’t support asynchronous execution.
    runBlocking {
        modelProducer.runTransaction {
            lineSeries { series(1, 5, 4, 7, 3, 14, 5, 9, 9, 14, 7, 13, 14, 4, 10, 12) }
            lineSeries { series(2, 6, 3, 8, 4, 15, 6, 10, 10, 15, 8, 14, 15, 5, 11, 13) }
            extras { it[LegendLabelKey] = setOf("Polar", "Smartwatch") }
        }
    }
    ScreenContent(
        record = RecordEntity(
            id = 1L,
            title = "Sample Recording",
            description = "This is a sample recording description.",
            starRecordingMilli = System.currentTimeMillis(),
            stopRecordingMilli = System.currentTimeMillis() + 390000
        ),
        downloadDataset = {},
        shareDataset = {},
        modelProducer = modelProducer,
        heartRatePolar = listOf(
            HeartRateGenericData(heartRate = 60, timestamp = System.currentTimeMillis()),
            HeartRateGenericData(heartRate = 62, timestamp = System.currentTimeMillis() + 1000),
            HeartRateGenericData(heartRate = 61, timestamp = System.currentTimeMillis() + 2000)
        ),
        heartRateSmartWatch = listOf(
            HeartRateGenericData(heartRate = 58, timestamp = System.currentTimeMillis()),
            HeartRateGenericData(heartRate = 59, timestamp = System.currentTimeMillis() + 1000),
            HeartRateGenericData(heartRate = 60, timestamp = System.currentTimeMillis() + 2000)
        )
    )
}