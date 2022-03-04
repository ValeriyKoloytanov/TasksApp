package com.koloytanov.tasksApp.fragment


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.datetime.dateTimePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.koloytanov.tasksApp.R
import com.koloytanov.tasksApp.TaskApp
import com.koloytanov.tasksApp.databinding.FragmentAddTaskfragmentBinding
import com.koloytanov.tasksApp.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class AddTaskFragment : Fragment() {
    private var _binding: FragmentAddTaskfragmentBinding? = null
    private val binding get() = _binding!!
    var sdf2: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    private val viewModel: ToDoViewModel by navGraphViewModels(R.id.nested_graph) {
        TaskModelFactory((activity?.application as TaskApp).repository)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_confirm -> {
                when (viewModel.task) {
                    null -> MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Добавление задачи")
                        .setMessage("Вы хотите  добавить задачу ")
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Да") { _, _ ->
                            lifecycleScope.launch(Dispatchers.IO)
                            {
                                viewModel.insert(
                                    Task(
                                        (0..100000).random(),
                                        LocalDateTime.parse(binding.textViewstart.text, sdf2)
                                            .atZone(ZoneId.systemDefault()).toInstant(),
                                        LocalDateTime.parse(binding.textViewend.text, sdf2)
                                            .atZone(ZoneId.systemDefault()).toInstant(),
                                        binding.taskName.editText?.text.toString(),
                                        binding.description.editText?.text.toString()
                                    )
                                )
                            }
                            findNavController().navigate(R.id.toDoListFragment)
                        }
                        .show()

                    else -> MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Обновление параметров")
                        .setMessage("Вы хотите применить изменения  ")
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton("Да") { _, _ ->
                            viewModel.task = Task(
                                viewModel.task!!.id, LocalDateTime.parse(binding.textViewstart.text, sdf2).atZone(ZoneId.systemDefault()).toInstant(),
                                LocalDateTime.parse(binding.textViewend.text, sdf2)
                                    .atZone(ZoneId.systemDefault()).toInstant(),
                                binding.taskName.editText?.text.toString(),
                                binding.description.editText?.text.toString()
                            )
                            lifecycleScope.launch(Dispatchers.IO)
                            {
                                viewModel.update(viewModel.task!!)

                            }
                            findNavController().navigate(R.id.toDoListFragment)

                        }
                        .show()

                }



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
        _binding = FragmentAddTaskfragmentBinding.inflate(layoutInflater)

        if (viewModel.task != null) {
            binding.taskName.editText?.setText(viewModel.task!!.name)
            binding.description.editText?.setText(viewModel.task!!.description)
            binding.textViewend.text =
                LocalDateTime.ofInstant(viewModel.task!!.date_finish, ZoneId.systemDefault())
                    .truncatedTo(
                        ChronoUnit.MINUTES
                    ).toString().replace("T", " ")
            binding.textViewstart.text =
                LocalDateTime.ofInstant(viewModel.task!!.date_start, ZoneId.systemDefault())
                    .truncatedTo(
                        ChronoUnit.MINUTES
                    ).toString().replace("T", " ")


        }
        binding.buttonStart.setOnClickListener {
            MaterialDialog(requireContext()).title(null, "Установите дату и время начала события ")
                .show {
                    dateTimePicker(
                        requireFutureDateTime = false,
                        show24HoursView = true
                    ) { _, dateTime ->
                        binding.textViewstart.text = sdf2.format(
                            LocalDateTime.ofInstant(
                                dateTime.toInstant(),
                                ZoneId.systemDefault()
                            )
                        )
                    }
                }

        }
        binding.buttonEnd.setOnClickListener {
            MaterialDialog(requireContext()).title(null, "Установите дату и время конца  события ")
                .show {
                    dateTimePicker(
                        requireFutureDateTime = false,
                        show24HoursView = true,
                        autoFlipToTime = false
                    ) { _, dateTime ->
                        binding.textViewend.text = sdf2.format(
                            LocalDateTime.ofInstant(
                                dateTime.toInstant(),
                                ZoneId.systemDefault()
                            )
                        )
                    }
                }
            binding.buttonStart.setOnClickListener {
                MaterialDialog(requireContext()).title(
                    null,
                    "Установите дату и время начала  события "
                ).show {
                    dateTimePicker(
                        requireFutureDateTime = false,
                        show24HoursView = true,
                        autoFlipToTime = false
                    ) { _, dateTime ->
                        binding.textViewstart.setText(
                            sdf2.format(
                                LocalDateTime.ofInstant(
                                    dateTime.toInstant(),
                                    ZoneId.systemDefault()
                                )
                            )
                        ).toString()
                    }
                }

            }
        }

        return binding.root

    }
}
