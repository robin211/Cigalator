package com.dputera.cigalator.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dputera.cigalator.ExpandableRecordData
import com.dputera.cigalator.R
import kotlinx.android.synthetic.main.expandable_child_item.view.*
import kotlinx.android.synthetic.main.expandable_grand_child_item.view.*
import kotlinx.android.synthetic.main.expandable_parent_item.view.*
import kotlinx.android.synthetic.main.expandable_parent_item.view.text

class ExpendableRecordAdapter(
    var context: Context, var recordList: MutableList<ExpandableRecordData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ExpandableRecordData.PARENT -> {
                RecordParentViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.expandable_parent_item, parent, false
                    )
                )
            }

            ExpandableRecordData.CHILD -> {
                RecordChildViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.expandable_child_item, parent, false
                    )
                )
            }

            ExpandableRecordData.GRANDCHILD -> {
                RecordGrandChildViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.expandable_grand_child_item, parent, false
                    )
                )
            }

            else -> {
                RecordParentViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.expandable_parent_item, parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int = recordList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = recordList[position]
        when (row.type) {
            ExpandableRecordData.PARENT -> {
                (holder as RecordParentViewHolder).yearName.text = row.recordParent.year.toString()
                if (row.isExpanded){
                    holder.upParent.visibility = View.VISIBLE
                    holder.downParent.visibility = View.GONE
                }else{
                    holder.downParent.visibility = View.VISIBLE
                    holder.upParent.visibility = View.GONE
                }
                holder.layout.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        holder.downParent.visibility = View.VISIBLE
                        holder.upParent.visibility = View.GONE
//                        holder.yearName.setTextColor(context.resources.getColor(R.color.colorTextDark))
                        collapseRow(position)
                    } else {
                        row.isExpanded = true
                        holder.upParent.visibility = View.VISIBLE
                        holder.downParent.visibility = View.GONE
//                        holder.yearName.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
                        expandRow(position)
                    }
                }
            }
            ExpandableRecordData.CHILD -> {
                (holder as RecordChildViewHolder).monthName.text = row.recordChild.month
                if (row.isExpanded){
                    holder.up.visibility = View.VISIBLE
                    holder.down.visibility = View.GONE
//                    holder.monthName.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
                }else{
                    holder.down.visibility = View.VISIBLE
                    holder.up.visibility = View.GONE
//                    holder.monthName.setTextColor(context.resources.getColor(R.color.colorTextDark))
                }
                holder.layout.setOnClickListener {
                    if (row.isExpanded) {
                        row.isExpanded = false
                        holder.down.visibility = View.VISIBLE
                        holder.up.visibility = View.GONE
//                        holder.monthName.setTextColor(context.resources.getColor(R.color.colorTextDark))
                        collapseRow(position)
                    } else {
                        row.isExpanded = true
                        holder.up.visibility = View.VISIBLE
                        holder.down.visibility = View.GONE
//                        holder.monthName.setTextColor(context.resources.getColor(R.color.colorPrimaryDark))
                        expandRow(position)
                    }
                }
            }
            ExpandableRecordData.GRANDCHILD -> {
                (holder as RecordGrandChildViewHolder).time.text = row.recordGrandChild.time
                if (row.recordGrandChild.isMood) holder.moodEvent.text = "Mood:"
                else holder.moodEvent.text = "Event:"
                holder.reason.text = row.recordGrandChild.reason
            }
        }
    }

    override fun getItemViewType(position: Int): Int = recordList[position].type

    private fun expandRow(position: Int) {
        val row = recordList[position]
        var nextPosition = position
        when (row.type) {
            ExpandableRecordData.PARENT -> {
                for (child in row.recordParent.months) {
                    recordList.add(
                        ++nextPosition,
                        ExpandableRecordData(ExpandableRecordData.CHILD, child)
                    )
                }
                notifyDataSetChanged()
            }
            ExpandableRecordData.CHILD -> {
                for (grandChild in row.recordChild.cigTimes) {
                    recordList.add(
                        ++nextPosition,
                        ExpandableRecordData(ExpandableRecordData.GRANDCHILD, grandChild)
                    )
                }
                notifyDataSetChanged()
            }
            ExpandableRecordData.GRANDCHILD -> {
                notifyDataSetChanged()
            }
        }
    }

    private fun collapseRow(position: Int) {
        val row = recordList[position]
        val nextPosition = position + 1
        when (row.type) {
            ExpandableRecordData.PARENT -> {
                outerloop@ while (true) {
                    //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")

                    if (nextPosition == recordList.size || recordList[nextPosition].type == ExpandableRecordData.PARENT) {
                        break@outerloop
                    }

                    recordList.removeAt(nextPosition)
                }

                notifyDataSetChanged()
            }

            ExpandableRecordData.CHILD -> {
                outerloop@ while (true) {
                    //  println("Next Position during Collapse $nextPosition size is ${shelfModelList.size} and parent is ${shelfModelList[nextPosition].type}")

                    if (nextPosition == recordList.size || recordList[nextPosition].type == ExpandableRecordData.CHILD) {
                        break@outerloop
                    }

                    recordList.removeAt(nextPosition)
                }

                notifyDataSetChanged()
            }
        }
    }

    class RecordParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var layout = itemView.parent_holder
        internal var downParent: ImageView = itemView.down_parent
        internal var upParent: ImageView = itemView.up_parent
        internal var yearName: TextView = itemView.text
    }

    class RecordChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var layout = itemView.child_holder
        internal var down: ImageView = itemView.down
        internal var up: ImageView = itemView.up
        internal var monthName: TextView = itemView.text
    }

    class RecordGrandChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var layout = itemView.grand_child_holder
        internal var time: TextView = itemView.text
        internal var moodEvent: TextView = itemView.mood_event
        internal var reason: TextView = itemView.reason
    }
}