package com.canteenapp.views.customer.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canteenapp.R
import com.canteenapp.adapter.ProductAdapter
import com.canteenapp.repository.FirebaseDatabaseRepository
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val database = FirebaseDatabaseRepository.getInstance()
    private lateinit var root: View
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var productListView: RecyclerView
    private lateinit var productListAdapter: RecyclerView.Adapter<*>
    private lateinit var productListManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        productListManager = GridLayoutManager(this.context, 2)
        productListView = root.findViewById(R.id.store_item_list)

        homeViewModel.getProducts().observe(viewLifecycleOwner, {
            productListAdapter = ProductAdapter(it) {
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

            productListView.apply {
                setHasFixedSize(true)
                layoutManager = productListManager
                adapter = productListAdapter
            }
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getProducts().observe(viewLifecycleOwner, {
            productListView.adapter = ProductAdapter(it) {
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