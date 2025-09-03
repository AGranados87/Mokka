import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DurationWheelPicker(
    selectedTime: Int,
    onTimeSelected: (Int) -> Unit,
    options: List<Int> = listOf(1, 50),
    visibleItems: Int = 3
) {
    val listState = rememberLazyListState(options.indexOf(selectedTime))
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .height((visibleItems * 40).dp)
            .width(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = ((visibleItems / 2) * 40).dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(options) { index, time ->
                val isSelected = listState.firstVisibleItemIndex == index
                Text(
                    text = "$time min",
                    fontSize = if (isSelected) 24.sp else 16.sp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        LaunchedEffect(remember { derivedStateOf { listState.firstVisibleItemIndex } }) {
            val newTime = options.getOrNull(listState.firstVisibleItemIndex) ?: selectedTime
            onTimeSelected(newTime)
        }
    }
}
