package com.storeapp.views.customer.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.storeapp.R
import com.storeapp.adapter.ProductAdapter
import com.storeapp.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private lateinit var root: View
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var searchListView: RecyclerView
    private lateinit var searchListAdapter: RecyclerView.Adapter<*>
    private lateinit var searchListViewManager: GridLayoutManager
    private lateinit var searchInput: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_search, container, false)

        searchInput = root.findViewById(R.id.search_input)
        searchInput.setOnEditorActionListener { v, actionId, _ ->
            if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {
                launchSearch(v.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

        searchListView = root.findViewById(R.id.search_list)
        searchListViewManager = GridLayoutManager(this.context, 2)
        searchViewModel.getSearchResults(null).observe(viewLifecycleOwner, {
            searchListAdapter = ProductAdapter(it) {
                lifecycleScope.launch {
                    val result = database.addToBag(it)
                    if (result) {
                        Toast.makeText(root.context, "Added item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    } else {
                        Toast.makeText(root.context, "Unable to add item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    }
                }
            }

            searchListView.apply {
                setHasFixedSize(true)
                layoutManager = searchListViewManager
                adapter = searchListAdapter
            }
        })

        return root
    }

    private fun launchSearch(search: String?) {
        var searchString = search
        if ( searchString === "" ) searchString = null
        searchViewModel.getSearchResults(searchString).observe(viewLifecycleOwner, {
            searchListView.adapter = ProductAdapter(it) {
                lifecycleScope.launch {
                    val result = database.addToBag(it)
                    if (result) {
                        Toast.makeText(root.context, "Added item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    } else {
                        Toast.makeText(root.context, "Unable to add item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        searchViewModel.getSearchResults(null).observe(viewLifecycleOwner, {
            searchListView.adapter = ProductAdapter(it) {
                lifecycleScope.launch {
                    val result = database.addToBag(it)
                    if (result) {
                        Toast.makeText(root.context, "Added item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    } else {
                        Toast.makeText(root.context, "Unable to add item", Toast.LENGTH_SHORT).run {
                            show()
                        }
                    }
                }
            }
        })
    }
}