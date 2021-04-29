package edu.cmu.sleepmonitor.sleeptracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import edu.cmu.sleepmonitor.R
import edu.cmu.sleepmonitor.database.SleepNight
import edu.cmu.sleepmonitor.databinding.ListItemSleepNightBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ITEM_VIEW_TYPE_HEADER = 0
private val ITEM_VIEW_TYPE_ITEM = 1
//class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.ViewHolder>()
class SleepNightAdapter(val clickListener: SleepNightListener) : ListAdapter<DataItem,
        RecyclerView.ViewHolder>(SleepNightDiffCallback()) {
    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun addHeaderAndSubmitList(list: List<SleepNight>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DataItem.Header)
                else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
            }
            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder {
        //return ViewHolder.from(parent)
         return when (viewType) {
             ITEM_VIEW_TYPE_HEADER -> TextViewHolder.from(parent)
             ITEM_VIEW_TYPE_ITEM -> ViewHolder.from(parent)
             else -> throw ClassCastException("Unknown viewType ${viewType}")
         }
    }

     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
         when (holder) {
             is ViewHolder -> {
                 val nightItem = getItem(position) as DataItem.SleepNightItem
                 holder.bind(clickListener, nightItem.sleepNight)
             }
         }
        //val item = data[position]
        //val item = getItem(position)
        //val res = holder.itemView.context.resources
        //holder.textView.text = item.sleepQuality.toString()
        //holder.bind(clickListener,item)
        /***move to ViewHolder Class***/
        /***if (item.sleepQuality <= 1) {
        holder.textView.setTextColor(Color.RED)
        } else {
        holder.textView.setTextColor(Color.BLACK)
        }
        holder.textView.text = item.sleepQuality.toString()***/
        //holder.sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
    }

    /***var data =  listOf<SleepNight>()
        set(value) {
            field = value
            //notifyDataSetChanged()
        }***/

   //override fun getItemCount() = data.size

    class TextViewHolder(view: View): RecyclerView.ViewHolder(view) {
        companion object {
            fun from(parent: ViewGroup): TextViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater.inflate(R.layout.header, parent, false)
                return TextViewHolder(view)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DataItem.SleepNightItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    class ViewHolder private constructor(val binding: ListItemSleepNightBinding)
        : RecyclerView.ViewHolder(binding.root) {
        //val sleepQuality: TextView = itemView.findViewById(R.id.sleep_length)
        //val sleepLength: TextView = binding.sleepLength //itemView.findViewById(R.id.sleep_length)
        //val quality: TextView = binding.qualityString //itemView.findViewById(R.id.quality_string)
        //val qualityImage: ImageView = binding.qualityImage //itemView.findViewById(R.id.quality_image)
        /***
        fun bind(item:SleepNight) {
            val res = itemView.context.resources
            binding.sleepLength.text =
                convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            binding.qualityString.text = convertNumericQualityToString(item.sleepQuality, res)
            binding.qualityImage.setImageResource(
                when (item.sleepQuality) {
                    0 -> R.drawable.ic_sleep_0
                    1 -> R.drawable.ic_sleep_1
                    2 -> R.drawable.ic_sleep_2
                    3 -> R.drawable.ic_sleep_3
                    4 -> R.drawable.ic_sleep_4
                    5 -> R.drawable.ic_sleep_5
                    else -> R.drawable.ic_sleep_active
                }
            )
        } ***/

        //fun bind(item: SleepNight) {
        fun bind(clickListener: SleepNightListener, item: SleepNight) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                //val layoutInflater = LayoutInflater.from(parent.context)
                //val view = layoutInflater
                //    .inflate(R.layout.list_item_sleep_night, parent, false) //as TextView
                //return ViewHolder(view)
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding
                    .inflate(layoutInflater, parent, false)
                return ViewHolder(binding)

            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minumum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}

sealed class DataItem {
    data class SleepNightItem(val sleepNight: SleepNight): DataItem() {
        override val id = sleepNight.nightId
    }

    object Header: DataItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}

