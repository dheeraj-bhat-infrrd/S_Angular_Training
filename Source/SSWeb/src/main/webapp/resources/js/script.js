function validateForm(id){
    var validate = true;
    $('#'+id).find('input').each(function(){
        if($(this).data('non-empty') == true){
            if($(this).val() == ""){
                $(this).parent().addClass('input-error');
                validate = false;
            }else{
                $(this).parent().removeClass('input-error');
            }
        }               
    });
    if(!validate){
        return false;
    }else{
        /* Form validated. */
        return true;
    }
}