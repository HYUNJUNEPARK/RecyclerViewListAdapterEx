package com.example.room

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.room.databinding.ActivityMainBinding
import com.example.room.db.AppDatabase
import com.example.room.db.Memo
import com.example.room.vm.MemoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var memoAdapter: MemoAdapter
    private lateinit var db: AppDatabase
    private val viewModel: MemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        try {
            db = AppDatabase.getInstance(this)!!
            memoAdapter = MemoAdapter()
            memoAdapter.memoDao = db.memoDao()
            binding.recyclerView.adapter = memoAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this)
            binding.mainActivity = this

            viewModel.getDbData(this)
            viewModel.memoList.observe(this) {
                memoAdapter.submitList(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onUpdate() {
        if (binding.editMemo.text.isNotEmpty()) {
            try {
                val memo = Memo(
                    idx = memoAdapter.currentList.size,
                    content = binding.editMemo.text.toString(),
                    datetime = System.currentTimeMillis()
                )
                //DB에 item 추가
                CoroutineScope(Dispatchers.IO).launch {
                    db.memoDao().insert(memo)
                }
                //currentList 갱신
                val memoList = memoAdapter.currentList.toMutableList().apply {
                    this.add(memo)
                }
                memoAdapter.submitList(memoList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}