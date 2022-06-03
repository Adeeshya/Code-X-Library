package com.example.libraryappcodex

import android.widget.Filter

class FilterPdf:Filter {
    //arraylist in which we want to search
    var filterList:ArrayList<Modelpdf>
    //adapter in which filter need to be implemented
    var adapterPdf:AdapterPdf

    //constructor
    constructor(filterList: ArrayList<Modelpdf>, adapterPdf: AdapterPdf) {
        this.filterList = filterList
        this.adapterPdf = adapterPdf
    }

    override fun performFiltering(constraint :CharSequence?) : FilterResults {
        var constraint: CharSequence? = constraint //value to search
        var results = FilterResults()

        //value to be searched should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()){
            //searched value is nor null not empty

            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<Modelpdf>()
            for (i in filterList.indices){
                //validate if match
                if (filterList[i].bookname.lowercase().contains(constraint)){
                    //searched value is similar to value in list, add filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //searched value is either null or empty,return all data
            results.count = filterList.size
            results.values = filterList
        }
        return results//dont miss
    }

    override fun publishResults(constraint: CharSequence, results:FilterResults){
        //apply filter changes
        adapterPdf.pdfArrayList = results.values as ArrayList<Modelpdf>


        //notify changes
        adapterPdf.notifyDataSetChanged()
    }
}