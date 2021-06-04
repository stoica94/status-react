from tests.base_test_case import SingleDeviceTestCase

class TestT(SingleDeviceTestCase):

    def test_api(self):
        trans = self.network_api.find_transaction_by_hash(transaction_hash='0xfb1a0d68cdd6bd6cb65a5e44ceabbb99ed806efdb60776ded5fbd36b5c47ea16')
        import pprint
        pprint.pprint(trans['blockHash'])
        print('*****NOT VALID*****')
        pprint.pprint(self.network_api.find_transaction_by_hash(transaction_hash='0xfb1a0d68cdd6bd6cb65a5e44ceabbb99ed806efdb60776ded5fbd36bea16'))
