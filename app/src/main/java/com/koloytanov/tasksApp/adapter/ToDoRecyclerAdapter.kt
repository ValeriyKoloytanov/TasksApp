package com.koloytanov.tasksApp.adapter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.harikrishnant1991.sectionedrv.SectionedRecyclerAdapter
import com.koloytanov.tasksApp.R
import com.koloytanov.tasksApp.databinding.HeaderTodoTypeBinding
import com.koloytanov.tasksApp.databinding.ItemActiveTodoBinding
import com.koloytanov.tasksApp.model.Task
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


class ToDoRecyclerAdapter :
    SectionedRecyclerAdapter<ToDoRecyclerAdapter.HeaderViewHolder, RecyclerView.ViewHolder>() {

    private val HEADERVIEWTYPE = 10
    private val ACTIVE_TODO_VIEW_TYPE = 20

    override fun getSectionCount(): Int {
        return 22
    }

    private val TaskMap = HashMap<String, ArrayList<Task>>(22)

    init {
        for (section in Sections.values())
            TaskMap[section.period] = ArrayList()
    }

    private fun getPeriodTasks(Section: Sections): ArrayList<Task> {
        return TaskMap[Section.period]!!
    }

    override fun getChildCount(section: Int): Int {
        return getPeriodTasks(Sections.values()[section]).size

    }

    private fun addTask(task: Task, date: LocalDate) {
        val start = LocalDateTime.ofInstant(task.date_start, ZoneId.systemDefault())
        val end = LocalDateTime.ofInstant(task.date_finish, ZoneId.systemDefault())

        val durarion = Duration.between(date.atStartOfDay(), end)
        if (!(date.isBefore(start.toLocalDate()) || date.isAfter(end.toLocalDate()))) {
            when {
                durarion.toDays() == Duration.between(task.date_finish, task.date_finish).toDays() && (Duration.between(task.date_start, task.date_finish).toDays()>=1) -> {
                    for (i in 0..end.hour) {

                        if (i == 0)
                            TaskMap[Sections.ZERO.period]?.add((task))
                        else {
                            TaskMap[Sections.values()[i].period]?.add((task))
                        }
                    }
                }
                (durarion.toDays() == Duration.between(task.date_start, task.date_finish)
                    .toDays()) && (Duration.between(task.date_start, task.date_finish).toDays()>=1) -> {
                    var hour = start.hour
                    while (hour != 22) {
                        TaskMap[Sections.values()[hour].period]?.add((task))
                        hour++

                    }
                }
                durarion.toDays() >= 1 -> {
                    var hour = 0
                    while (hour != 22) {
                        TaskMap[Sections.values()[hour].period]?.add((task))
                        hour++
                    }
                }
                (durarion.toHours() <= 24L) && (Duration.between(task.date_start, task.date_finish).toDays()<1)-> {

                    for (i in start.hour..end.hour) {

                        if (i == 0)
                            TaskMap[Sections.ZERO.period]?.add((task))
                        else {
                            TaskMap[Sections.values()[i].period]?.add((task))
                        }
                    }
                }
            }
        }

    }

    private fun clearTaskMap() {
        for (section in Sections.values())
            TaskMap[section.period]?.clear()
    }

    fun submitList(newtasks: List<Task>, date: LocalDate) {
        clearTaskMap()
        newtasks.forEach { addTask(it, date) }
        notifyDataSetChanged()
    }

    override fun getHeaderViewType(section: Int): Int {
        return HEADERVIEWTYPE
    }

    override fun getChildViewType(section: Int, index: Int): Int {
        return ACTIVE_TODO_VIEW_TYPE
    }

    override fun onBindHeader(holder: HeaderViewHolder, section: Int) {
        (holder as? HeaderViewHolder)?.bind(Sections.values()[section].period)

    }

    override fun onBindChild(holder: RecyclerView.ViewHolder, section: Int, index: Int) {
        (holder as? ActiveToDoViewHolder)?.bind(getPeriodTasks(Sections.values()[section])[index])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            HEADERVIEWTYPE -> {
                val binding: HeaderTodoTypeBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.header_todo_type,
                    parent,
                    false
                )
                return HeaderViewHolder(binding)
            }
            ACTIVE_TODO_VIEW_TYPE -> {
                val binding: ItemActiveTodoBinding = DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.item_active_todo,
                    parent,
                    false
                )
                return ActiveToDoViewHolder(binding)
            }
        }
        throw RuntimeException("Invalid view type")
    }

    class HeaderViewHolder(private val binding: HeaderTodoTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(title: String) {
            binding.title = title
        }
    }

    inner class ActiveToDoViewHolder(private val binding: ItemActiveTodoBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private lateinit var taskData: Bundle
        fun bind(task: Task) {
            binding.time = LocalDateTime.ofInstant(
                task.date_start,
                ZoneId.systemDefault()
            ).hour.toString() + "-" +
                    LocalDateTime.ofInstant(
                        task.date_finish,
                        ZoneId.systemDefault()
                    ).hour.toString()
            binding.title = task.name
            taskData = bundleOf("id" to task.id)
        }

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            p0?.findNavController()
                ?.setGraph(R.navigation.nav_graph, bundleOf("from_recycle_view" to true))
            p0?.findNavController()
                ?.navigate(R.id.action_toDoListFragment_to_nested_graph, taskData)
        }


    }
}