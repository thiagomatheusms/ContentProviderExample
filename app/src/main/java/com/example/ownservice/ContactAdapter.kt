package com.example.ownservice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ownservice.model.Contact
import kotlinx.android.synthetic.main.item_contact_list.view.*

class ContactAdapter(private var contacts: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.ContactAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactAdapterViewHolder {
        val idLayoutForInflater = R.layout.item_contact_list
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(idLayoutForInflater, parent, false)
        return ContactAdapterViewHolder(view)
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ContactAdapterViewHolder, position: Int) {
        val contact = contacts[position]
        holder.bind(contact)
    }

    fun updateList(contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    inner class ContactAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(contact: Contact) {
            itemView.tv_contact_name.text = contact.name
            itemView.tv_contact_cellphone.text = contact.cellphone
        }

    }
}