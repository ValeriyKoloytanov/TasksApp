package com.koloytanov.tasksApp.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.koloytanov.tasksApp.R
import com.koloytanov.tasksApp.TaskApp
import com.koloytanov.tasksApp.databinding.FragmentDetailedTaskBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit


class DetailedTaskFragment : Fragment() {

    private var _binding: FragmentDetailedTaskBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ToDoViewModel by navGraphViewModels(R.id.nested_graph) {
        TaskModelFactory((activity?.application as TaskApp).repository)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                findNavController().navigate(R.id.action_detailedTaskFragment_to_addTaskFragment)

                return true

            }
            R.id.action_delete -> {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удаление задачи")
                    .setMessage("Вы хотите удалить задачу ")
                    .setNegativeButton("Нет") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Да") { _, _ ->
                        lifecycleScope.launch(Dispatchers.IO)
                        {
                            viewModel.delete(viewModel.task!!)
                        }
                        findNavController().navigate(R.id.toDoListFragment)


                    }
                    .show()
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
        _binding = FragmentDetailedTaskBinding.inflate(layoutInflater)

        val id = arguments?.getInt("id")!!
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.task = viewModel.specific_task(id)
            this@DetailedTaskFragment.activity?.runOnUiThread {
                binding.NametextView.text = viewModel.task!!.name
                binding.DescriptionTextView.text = viewModel.task!!.description
                binding.EndDatetextView.text =
                    LocalDateTime.ofInstant(viewModel.task!!.date_finish, ZoneId.systemDefault())
                        .truncatedTo(
                            ChronoUnit.MINUTES
                        ).toString().replace("T", " ")
                binding.StartDatetextView.text =
                    LocalDateTime.ofInstant(viewModel.task!!.date_start, ZoneId.systemDefault())
                        .truncatedTo(
                            ChronoUnit.MINUTES
                        ).toString().replace("T", " ")


            }
        }




        return binding.root
    }
}