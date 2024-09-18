package com.standalone.migrationwithcompose

//import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.standalone.migrationwithcompose.db.AppDatabase
import com.standalone.migrationwithcompose.db.Item
//import com.standalone.migrationwithcompose.di.ComposeApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainScreenViewModel(
    database: AppDatabase
): ViewModel() {

    var items: MutableState<List<Item>> = mutableStateOf(listOf())
        private set

    private val scope = viewModelScope
    private val dao = database.itemsDao()

    init {
        scope.launch(Dispatchers.IO) {
            var items = dao.getItems()
            if (items.isEmpty()) {
                val dtoItems = simulateNetworkCall()
                dao.upsertItems(dtoItems)
                items = dao.getItems()
            }
            this@MainScreenViewModel.items.value = items
        }
    }

    private suspend fun simulateNetworkCall():List<Item> {
        return withContext(Dispatchers.IO) {
            delay(200)
            val result = mutableListOf<Item>()
            for (i in 1..10) {
                result.add(
                    Item(
                        id = i,
                        text = "This is element #$i"
                    )
                )
            }
            result.toList()
        }
    }

}