package com.example.joymap.Services

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import com.example.joymap.ChildrenActivity
import com.example.joymap.Models.Child
import com.example.joymap.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ChildrenAdapter(
    private val context: Context,
    private val children: List<Child>
) : BaseAdapter() {

    private val aliases = loadAliases()

    override fun getCount(): Int = children.size

    override fun getItem(position: Int): Child = children[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.child_item, parent, false)
        val child = getItem(position)

        val aliasTextView = view.findViewById<TextView>(R.id.childAlias)
        val deviceIdTextView = view.findViewById<TextView>(R.id.childDeviceId)
        val batteryTextView = view.findViewById<TextView>(R.id.childBatteryLevel)

        val alias = aliases[child.id] ?: "Not Set"
        aliasTextView.text = "Alias: $alias"
        deviceIdTextView.text = "Device ID: ${child.deviceId}"
        batteryTextView.text = "Battery Level: ${child.battery}%"

        view.setOnClickListener {
            if (context is ChildrenActivity) {
                context.showAliasDialog(child.id)
            }
        }

        return view
    }

    private fun loadAliases(): Map<String, String> {
        val sharedPreferences = context.getSharedPreferences("ParentAppPrefs", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("childAliases", "{}")
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(json, type)
    }
}