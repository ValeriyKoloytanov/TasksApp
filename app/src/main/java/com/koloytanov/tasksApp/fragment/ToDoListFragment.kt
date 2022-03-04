package com.koloytanov.tasksApp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.koloytanov.tasksApp.R
import com.koloytanov.tasksApp.TaskApp
import com.koloytanov.tasksApp.adapter.ToDoRecyclerAdapter
import com.koloytanov.tasksApp.databinding.FragmentToDoListBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


class ToDoListFragment : Fragment() {

    private var _binding: FragmentToDoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ToDoViewModel by viewModels {
        TaskModelFactory((activity?.application as TaskApp).repository)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {

                findNavController().navigate(R.id.action_toDoListFragment_to_nested_graph)
                return true
            }
            else -> super.onOptionsItemSelected(item)

        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToDoListBinding.inflate(layoutInflater)
        val adapter = ToDoRecyclerAdapter()
        binding.recyclerView.adapter = adapter
        binding.datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = LocalDate.of(year, month + 1, dayOfMonth)
            val daystart = date.atStartOfDay()
            val dayend = LocalDateTime.of(date, LocalTime.MAX)
            binding.scrollView.isScrollbarFadingEnabled = true
            binding.viewModel = viewModel
            viewModel.Notes.observe(viewLifecycleOwner) { notes ->
                adapter.submitList(notes, daystart, dayend)
            }

        }
        return binding.root

    }
}