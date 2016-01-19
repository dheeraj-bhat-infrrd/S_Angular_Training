using EncompassWebAPI.Models;
using System;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace EncompassWebAPI.Controllers
{
    public class EncompassController : ApiController
    {
       
        [HttpPost]
        public HttpResponseMessage ConnectionStatus(EncompassCredentials encompassCredentials)
        {
            //check if null object is passed
            if (null == encompassCredentials)
            {
                encompassCredentials = new EncompassCredentials();
                encompassCredentials.status = false;
                encompassCredentials.message = "Passed parameter is null or empty";
                return Request.CreateResponse(HttpStatusCode.OK, encompassCredentials);
            }
            //check if null or empty userName is passed
            if (null == encompassCredentials.userName || encompassCredentials.userName == "")
            {
                encompassCredentials.status = false;
                encompassCredentials.message = "Passed parameter userName is null or empty";
                return Request.CreateResponse(HttpStatusCode.OK, encompassCredentials);
            }
            //check if null or empty passwod is passed
            if (null == encompassCredentials.password || encompassCredentials.password == "")
            {
                encompassCredentials.status = false;
                encompassCredentials.message = "Passed parameter password is null or empty";
                return Request.CreateResponse(HttpStatusCode.OK, encompassCredentials);
            }
            
            //create session object
            EllieMae.Encompass.Client.Session s = null;

            try
            {
                s = new EllieMae.Encompass.Client.Session();

                var encompassURL = encompassCredentials.clientUrl;
                var encompassUserName = encompassCredentials.userName;
                var encompassPassword = encompassCredentials.password;

                if (null == encompassURL || encompassURL == "")
                    s.StartOffline(encompassUserName, encompassPassword);
                else
                    s.Start(encompassURL, encompassUserName, encompassPassword);

                encompassCredentials.status = true;
                encompassCredentials.message = "Connected successfully";
                return Request.CreateResponse(HttpStatusCode.OK, encompassCredentials );
            }
            catch (Exception ex)
            {
                encompassCredentials.status = false;
                encompassCredentials.message = "Connection Unsuccessful. Reason : " + ex.Message;
                return Request.CreateResponse(HttpStatusCode.OK, encompassCredentials);
            }
            finally
            {
                if (null != s && s.IsConnected)
                {
                    s.End();
                }
            }

        }

    }
}