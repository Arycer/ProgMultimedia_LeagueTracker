package me.arycer.leaguetracker.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import me.arycer.leaguetracker.R
import me.arycer.leaguetracker.model.FavouriteProfile
import java.util.UUID

class EditProfileDialog(context: Context, private val user: FavouriteProfile?, private val onUpdated: (FavouriteProfile) -> Unit) : Dialog(context) {
    private lateinit var nameEditText: EditText
    private lateinit var taglineEditText: EditText
    private lateinit var regionSpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_edit_profile)

        nameEditText = findViewById(R.id.profile_name)
        nameEditText.setText(user?.name)

        taglineEditText = findViewById(R.id.profile_tag)
        taglineEditText.setText(user?.tagline)

        regionSpinner = findViewById(R.id.region_spinner)
        regionSpinner.setSelection(user?.region?.ordinal ?: 0)

        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)

        val regions = FavouriteProfile.Region.entries.toTypedArray()
        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, regions)
        regionSpinner.adapter = spinnerAdapter

        saveButton.setOnClickListener {
            val updatedName = nameEditText.text.toString().trim()
            val updatedTagline = taglineEditText.text.toString().trim()

            if (updatedName.isEmpty() || updatedTagline.isEmpty()) {
                nameEditText.error = "Nombre no puede estar vacío"
                taglineEditText.error = "Tagline no puede estar vacío"
                return@setOnClickListener
            }

            val updatedRegion = FavouriteProfile.Region.entries[regionSpinner.selectedItemPosition]
            val updatedUser = FavouriteProfile(user?.id ?: UUID.randomUUID().toString(), updatedName, updatedTagline, updatedRegion)

            onUpdated(updatedUser)
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }
    }
}
