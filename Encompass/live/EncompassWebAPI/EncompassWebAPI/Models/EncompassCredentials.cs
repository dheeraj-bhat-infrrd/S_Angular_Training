
namespace EncompassWebAPI.Models
{
    public class EncompassCredentials
    {
        public string userName { get; set; }
        public string password { get; set; }
        public string clientUrl { get; set; }

        public bool status { get; set; }
        public string message { get; set; }

    }
}