package com.example.ownservice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ownservice.model.Contact
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var contactsList = mutableListOf<Contact>()
    private val contactAdapter by lazy {
        ContactAdapter(emptyList<Contact>())
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        checkIfHasPermissions()
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_contacts.apply {
            adapter = contactAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun checkIfHasPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                getContacts()
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    getContacts()
                } else {
                    Toast.makeText(
                        this,
                        "Algo deu errado ao tentar permitir que você tenha acesso a sua lista de contatos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun getContacts() {
        val uri = ContactsContract.Contacts.CONTENT_URI
        val sortBy = ContactsContract.Contacts.DISPLAY_NAME_SOURCE
        val cursor = contentResolver.query(uri, null, null, null, sortBy)
        var contact: Contact? = null

        while (cursor.count > 0 && cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            val name =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
            val hasPhoneNumber =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    .toInt()

            if (hasPhoneNumber > 0) {
                contact = Contact(name = name, cellphone = getPhoneNumberFromContact(id))
                contactsList.add(contact)
                contactAdapter.updateList(contactsList)
            }
        }
        cursor.close()
    }

    private fun getPhoneNumberFromContact(id: String): String {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val sortBy = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $id"
        val cursor = contentResolver.query(uri, null, null, null, sortBy)

        while (cursor.moveToNext()) {
            val cellphone =
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            cursor.close()
            return cellphone
        }
        return ""
    }
}
