package edu.cmu.sleepmonitor.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import edu.cmu.sleepmonitor.R
import edu.cmu.sleepmonitor.database.SleepDatabase
import edu.cmu.sleepmonitor.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        //application context
        val application = requireNotNull(this.activity).application
        //define datasource
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        //a viewModelFactory
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        // Get a reference to the ViewModel associated with this fragment.
        val sleepTrackerViewModel =
            ViewModelProvider(this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        sleepTrackerViewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observed state is true.
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_SHORT // How long to display the message.
                ).show()
                sleepTrackerViewModel.doneShowingSnackbar()
            }
        })

        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.setLifecycleOwner(this)
        sleepTrackerViewModel.navigateToSleepQuality.observe(this, Observer {
            night->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                sleepTrackerViewModel.doneNavigating()
            }
        })
        return binding.root
    }
}
