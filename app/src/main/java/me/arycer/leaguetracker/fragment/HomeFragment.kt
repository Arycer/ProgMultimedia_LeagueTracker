package me.arycer.leaguetracker.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import me.arycer.leaguetracker.R

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_to_fragmentA)?.setOnClickListener {
            findNavController().navigate(R.id.action_to_favusers)
        }
    }
}
