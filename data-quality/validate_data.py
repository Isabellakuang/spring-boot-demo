# validate_data.py
import great_expectations as ge

context = ge.get_context()

result = context.run_checkpoint(checkpoint_name="conversations")
if not result["success"]:
    raise SystemExit("Data quality check failed!")